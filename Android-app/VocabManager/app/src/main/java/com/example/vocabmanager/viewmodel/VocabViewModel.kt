package com.example.vocabmanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.switchMap
import com.example.vocabmanager.Repository
import com.example.vocabmanager.entities.Vocab
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import java.io.Serializable

class VocabViewModel(application: Application, private val savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    companion object {
        const val TAG = "VocabViewModel"
        const val TAG_SEARCH = "TAG_SEARCH"
        const val TAG_FILTER_LESSON = "TAG_FILTER_LESSON"
        const val TAG_FILTER_POS = "TAG_FILTER_POS"
    }

    val searchData: MutableLiveData<SearchBean?>
    var vocabData: MutableLiveData<List<Vocab>>
    var progressData: MutableLiveData<Boolean>?
    private var repository: Repository
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    var page = 1
    private var lastSearch: SearchBean? = null

    init {
        repository = Repository(application)
        progressData = MutableLiveData<Boolean>()
        savedStateHandle[TAG_SEARCH] = ""
        savedStateHandle[TAG_FILTER_LESSON] = ""
        savedStateHandle[TAG_FILTER_POS] = ""
        vocabData = MutableLiveData()
        searchData = savedStateHandle.getLiveData<String>(TAG_SEARCH)
            .switchMap { search ->
                savedStateHandle.getLiveData<String>(TAG_FILTER_LESSON)
                    .switchMap { lesson ->
                        savedStateHandle.getLiveData<String>(TAG_FILTER_POS)
                            .switchMap { poS ->
                                val obj = SearchBean(search, lesson, poS)
                                Log.d(TAG, "obj: $obj")
                                Log.d(TAG, "lastSearch: $lastSearch")
                                Log.d(TAG, "obj != lastSearch: ${obj != lastSearch}")
                                if(obj != lastSearch) {
                                    lastSearch = obj
                                    MutableLiveData(lastSearch)
                                } else {
                                    MutableLiveData(null)
                                }
                            }
                    }
            } as MutableLiveData
    }

    fun refresh() {
        page = 1
        vocabData.postValue(listOf())
        getData(getCurrentSearchBean())
    }

    fun getData(searchBean: SearchBean?, isLoadMore: Boolean = false) {
        if(searchBean == null) {
            vocabData.postValue(vocabData.value)
            return
        }
        Log.d(TAG, "getData: searchBean = $searchBean page $page")
        if(isLoadMore) {
            page++
            Log.d(TAG, "isLoadMore $page")
        }
        progressData?.value = true
        repository.getAllVocab(
            searchBean.search,
            searchBean.poS, searchBean.lesson, page
        ).subscribeWith(object : DisposableObserver<List<Vocab>>() {
            override fun onNext(t: List<Vocab>) {
                Log.d(TAG, "onNext: $t")
                val list: MutableList<Vocab> = mutableListOf()
                vocabData.value?.let {
                    list.addAll(it)
                }
                list.addAll(t)
                vocabData.postValue(list.distinct())
                progressData?.value = false
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "onError: $e")
                vocabData.postValue(listOf())
                progressData?.value = false
                if(isLoadMore) {
                    page--
                    Log.d(TAG, "onNext: isLoadMore $page")
                }
            }

            override fun onComplete() {
                Log.d(TAG, "onComplete: ")
                progressData?.value = false
            }
        })
    }

    fun getCurrentSearchBean(): SearchBean {
        return lastSearch!!
    }

    fun searchQuery(query: String) {
        page = 1
        vocabData.postValue(listOf())
        savedStateHandle[TAG_SEARCH] = query
    }

    fun filterLesson(id: String) {
        page = 1
        vocabData.postValue(listOf())
        savedStateHandle[TAG_FILTER_LESSON] = if (id == "-1") "" else id
    }

    fun filterPoS(id: String) {
        page = 1
        vocabData.postValue(listOf())
        savedStateHandle[TAG_FILTER_POS] = if (id == "-1") "" else id
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    data class SearchBean(val search: String = "", val lesson: String = "", val poS: String = "") :
        Serializable
}
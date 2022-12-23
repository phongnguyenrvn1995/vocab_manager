package com.example.vocabmanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.switchMap
import com.example.vocabmanager.Repository
import com.example.vocabmanager.entities.Lesson
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import java.io.Serializable

class LessonViewModel(application: Application, private val savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    companion object {
        const val TAG = "LessonViewModel"
        const val TAG_SEARCH = "TAG_SEARCH"
        const val TAG_FILTER_COURSE = "TAG_FILTER_COURSE"
        const val TAG_FILTER_STATUS = "TAG_FILTER_STATUS"
    }

    val searchData: MutableLiveData<SearchBean>
    var lessonData: MutableLiveData<List<Lesson>>?
    var progressData: MutableLiveData<Boolean>?
    private var repository: Repository
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        repository = Repository(application)
        progressData = MutableLiveData<Boolean>()
        savedStateHandle[TAG_SEARCH] = ""
        savedStateHandle[TAG_FILTER_COURSE] = ""
        savedStateHandle[TAG_FILTER_STATUS] = ""
        lessonData = MutableLiveData()
        searchData = savedStateHandle.getLiveData<String>(TAG_SEARCH)
            .switchMap { search ->
                savedStateHandle.getLiveData<String>(TAG_FILTER_COURSE).switchMap { course ->
                    savedStateHandle.getLiveData<String>(TAG_FILTER_STATUS).switchMap { status ->
                        MutableLiveData(SearchBean(search, course, status))
                    }
                }
            } as MutableLiveData<SearchBean>
    }

    fun getCurrentSearchBean(): SearchBean {
        return SearchBean(
            savedStateHandle[TAG_SEARCH] ?: "",
            savedStateHandle[TAG_FILTER_COURSE] ?: "",
            savedStateHandle[TAG_FILTER_STATUS] ?: ""
        )
    }


    fun getData(searchBean: SearchBean) {
        progressData?.value = true
        repository.getAllLessons(searchBean.search, searchBean.course, searchBean.status)
            .subscribeWith(object : DisposableObserver<List<Lesson>>() {
                override fun onNext(t: List<Lesson>) {
                    Log.d(TAG, "onNext: $t")
                    lessonData?.postValue(t)
                    progressData?.value = false
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: $e")
                    lessonData?.postValue(listOf())
                    progressData?.value = false
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: ")
                    progressData?.value = false
                }
            })
    }

    fun getLocalLessonById(id: Int): Lesson? {
        lessonData?.value?.filter {
            it.lessonId == id
        }?.forEach {
            return it
        }
        return null
    }

    fun searchQuery(query: String) {
        savedStateHandle[TAG_SEARCH] = query
    }

    fun filterCourse(id: String) {
        savedStateHandle[TAG_FILTER_COURSE] = if (id == "-1") "" else id
    }

    fun filterStatus(id: String) {
        savedStateHandle[TAG_FILTER_STATUS] = if (id == "-1") "" else id
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    data class SearchBean(
        val search: String = "",
        val course: String = "",
        val status: String = ""
    ) : Serializable
}
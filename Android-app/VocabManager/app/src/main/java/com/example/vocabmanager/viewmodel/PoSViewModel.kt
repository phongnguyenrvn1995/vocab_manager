package com.example.vocabmanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.switchMap
import com.example.vocabmanager.Repository
import com.example.vocabmanager.entities.PoS
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver

class PoSViewModel(application: Application, private val savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    companion object {
        const val TAG = "PoSViewModel"
        const val TAG_SEARCH = "TAG_SEARCH"
    }

    private var repository: Repository
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    var poSData: MutableLiveData<List<PoS>>?
    var progressData: MutableLiveData<Boolean>?
    var searchData: MutableLiveData<String>?

    init {
        repository = Repository(application)
        poSData = MutableLiveData()
        progressData = MutableLiveData<Boolean>()
        savedStateHandle[TAG_SEARCH] = ""
        searchData = savedStateHandle.getLiveData<String>(TAG_SEARCH)
            .switchMap { MutableLiveData(it) } as MutableLiveData<String>
    }

    fun getData(searchStr: String = "") {
        progressData?.value = true
        repository.getAllPoS(searchStr)
            .subscribeWith(object : DisposableObserver<List<PoS>>() {
                override fun onNext(t: List<PoS>) {
                    Log.d(TAG, "onNext: $t")
                    poSData?.postValue(t)
                    progressData?.value = false
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: $e")
                    poSData?.postValue(listOf())
                    progressData?.value = false
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: ")
                    progressData?.value = false
                }
            })
    }

    fun searchQuery(query: String) {
        savedStateHandle[LessonViewModel.TAG_SEARCH] = query
    }

    fun getCurrentSearch() = savedStateHandle.get<String>(TAG_SEARCH) ?: ""

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
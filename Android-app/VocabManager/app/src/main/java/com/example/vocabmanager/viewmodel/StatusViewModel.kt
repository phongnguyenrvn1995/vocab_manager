package com.example.vocabmanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.vocabmanager.Repository
import com.example.vocabmanager.entities.Status
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver

class StatusViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "StatusViewModel"
    }

    private var repository: Repository
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    var statusData: MutableLiveData<List<Status>>

    init {
        repository = Repository(application)
        compositeDisposable = CompositeDisposable()
        statusData = MutableLiveData()
        getData()
    }

    private fun getData() {
        val d = repository.getAllStatus()
            .subscribeWith(object : DisposableObserver<List<Status>>() {
                override fun onNext(listStatus: List<Status>) {
                    Log.d(TAG, "onNext: $listStatus")
                    statusData.postValue(listStatus)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: $e")
                    statusData.postValue(listOf())
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: ")
                }
            })
        compositeDisposable.add(d)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
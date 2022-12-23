package com.example.vocabmanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.vocabmanager.Repository
import com.example.vocabmanager.entities.Response
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver

class DeleteCourseViewModel(application: Application/*, val savedStateHandle: SavedStateHandle*/) :
    AndroidViewModel(
        application
    ) {

    companion object {
        const val TAG = "DeleteCourseViewModel"
    }

    private var repository: Repository
    private var compositeDisposable: CompositeDisposable
    var respData: MutableLiveData<Response>
    var errorData: MutableLiveData<Throwable>

    init {
        repository = Repository(application)
        compositeDisposable = CompositeDisposable()
        respData = MutableLiveData()
        errorData = MutableLiveData()
    }

    fun deleteCourse(id: Int) {
        Log.d(TAG, "deleteCourse: ")
        val d = repository.deleteCourse(id)
            .subscribeWith(object: DisposableObserver<Response>() {
                override fun onNext(t: Response) {
                    respData.postValue(t)
                }

                override fun onError(e: Throwable) {
                    errorData.postValue(e)
                }

                override fun onComplete() {
                }
            })
        compositeDisposable.add(d)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
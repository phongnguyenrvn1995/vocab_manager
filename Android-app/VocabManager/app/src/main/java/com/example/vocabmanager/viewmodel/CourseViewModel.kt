package com.example.vocabmanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.vocabmanager.Repository
import com.example.vocabmanager.entities.Course
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver

class CourseViewModel(application: Application, private val savedStateHandle: SavedStateHandle) :
    AndroidViewModel(
        application
    ) {
    companion object {
        const val TAG = "CourseViewModel"
        const val TAG_QUERY = "QUERY"
    }

    var courseData: MutableLiveData<List<Course>>?// = MutableLiveData<List<Course>>()
    var progressData: MutableLiveData<Boolean>?
    private var repository: Repository
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        repository = Repository(application)
        courseData = MutableLiveData()
        progressData = MutableLiveData<Boolean>()
    }

    fun setQuery(query: String = "") {
        Log.d(TAG, "setQuery: ")
        savedStateHandle[TAG_QUERY] = query
        progressData?.value = true
        val d = repository.getAllCourses(query)
            .subscribeWith(object : DisposableObserver<List<Course>>() {
                override fun onNext(t: List<Course>) {
                    courseData?.postValue(t)
                    progressData?.value = false
                }

                override fun onError(e: Throwable) {
                    courseData?.postValue(listOf())
                    progressData?.value = false
                }

                override fun onComplete() {
                    progressData?.value = false
                }
            })
        compositeDisposable.add(d)
    }

    fun getLocalCourseById(id: Int): Course? {
        courseData?.value?.filter {
            it.courseId == id
        }?.forEach {
            return it
        }
        return null
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
package com.example.vocabmanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.vocabmanager.Repository
import com.example.vocabmanager.entities.Lesson
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.entities.Status
import com.example.vocabmanager.entities.validate.LessonValidate
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver

class AddLessonViewModel(application: Application/*, val savedStateHandle: SavedStateHandle*/) :
    AndroidViewModel(
        application
    ) {

    companion object {
        const val TAG = "AddLessonViewModel"
    }

    private var repository: Repository
    private var compositeDisposable: CompositeDisposable
    var lessonValidateData: MutableLiveData<LessonValidate>
    var respData: MutableLiveData<Response>
    var errorData: MutableLiveData<Throwable>
    var lessonData: MutableLiveData<Lesson>

    init {
        repository = Repository(application)
        compositeDisposable = CompositeDisposable()
        lessonValidateData = MutableLiveData()
        respData = MutableLiveData()
        errorData = MutableLiveData()
        lessonData = MutableLiveData()
    }

    private fun validate(lesson: Lesson): Boolean {
        val validateObj = LessonValidate()
        if (lesson.lessonName == null || lesson.lessonName?.trim()?.isEmpty() == true)
            validateObj.isNamePassed = false
        if (lesson.lessonCourse == null || lesson.lessonCourse?.toString()?.isEmpty() == true)
            validateObj.isCourseIdPassed = false

        lessonValidateData.postValue(validateObj)
        return validateObj.isPassed()
    }

    fun saveLesson(lessonName: String, courseID: Int, isActive: Boolean) {
        val lesson = Lesson(
            lessonName = lessonName,
            lessonCourse = courseID,
            lessonStatus = if (isActive) Status.ACTIVE else Status.DE_ACTIVE
        )

        if (!validate(lesson)) {
            return
        }

        val d = repository.addLesson(lesson)
            .subscribeWith(object : DisposableObserver<Response>() {
                override fun onNext(t: Response) {
                    Log.d(TAG, "onNext: $t")
                    respData.postValue(t)
                    if(t.responseId == Response.SUCCESS) {
                        lessonData.postValue(lesson)
                    }
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: $e")
                    errorData.postValue(e)
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
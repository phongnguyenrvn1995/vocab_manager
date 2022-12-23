package com.example.vocabmanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.vocabmanager.Repository
import com.example.vocabmanager.entities.Course
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.entities.Status
import com.example.vocabmanager.entities.validate.CourseValidate
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver

class UpdateCourseViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "UpdateCourseViewModel"
    }

    private var repository: Repository
    private var compositeDisposable: CompositeDisposable
    var courseValidateData: MutableLiveData<CourseValidate>
    var respData: MutableLiveData<Response>
    var errorData: MutableLiveData<Throwable>
    var courseData: MutableLiveData<Course>

    init {
        repository = Repository(application)
        compositeDisposable = CompositeDisposable()
        courseValidateData = MutableLiveData()
        respData = MutableLiveData()
        errorData = MutableLiveData()
        courseData = MutableLiveData()
    }

    private fun validate(course: Course): Boolean {
        val courseValidateObj = CourseValidate()
        if (course.courseName == null || course.courseName?.trim()?.isEmpty() == true)
            courseValidateObj.isNamePassed = false
        if (course.courseDescription == null || course.courseDescription?.trim()?.isEmpty() == true)
            courseValidateObj.isDescPassed = false
        if (course.courseDateCreat == null || course.courseDateCreat?.trim()?.isEmpty() == true)
            courseValidateObj.isDateCreatePassed = false

        courseValidateData.postValue(courseValidateObj)
        return courseValidateObj.isPassed()
    }

    fun updateCourse(courseId: Int?,
                     courseName: String?,
                     courseDesc: String?,
                     courseDatetimeCreate: String?,
                     isActive: Boolean?) {
        Log.d(TAG, "updateCourse: ")
        val course = Course(
            courseId = courseId,
            courseName = courseName,
            courseDescription = courseDesc,
            courseDateCreat = courseDatetimeCreate,
            courseStatus = if (isActive == true) Status.ACTIVE else Status.DE_ACTIVE
        )

        if (!validate(course)) {
            return
        }

        val d = repository.updateCourse(course)
            .subscribeWith(object : DisposableObserver<Response>() {
                override fun onNext(t: Response) {
                    Log.d(AddCourseViewModel.TAG, "onNext: $t")
                    respData.postValue(t)
                    courseData.postValue(course)
                }

                override fun onError(e: Throwable) {
                    Log.d(AddCourseViewModel.TAG, "onError: $e")
                    errorData.postValue(e)
                }

                override fun onComplete() {
                    Log.d(AddCourseViewModel.TAG, "onComplete: ")
                }
            })
        compositeDisposable.add(d)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
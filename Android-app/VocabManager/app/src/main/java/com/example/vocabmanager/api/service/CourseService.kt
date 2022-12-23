package com.example.vocabmanager.api.service

import android.content.Context
import com.example.vocabmanager.api.GlobalUtils
import com.example.vocabmanager.api.consts.CommonException
import com.example.vocabmanager.entities.Course
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.vocabmanager.entities.Response as RestResponse

class CourseService(val context: Context) {
    fun getAllCourses(searchStr: String): Observable<List<Course>> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.CourseAPI::class.java)
            api.listCourse(searchStr).enqueue(object : Callback<List<Course>> {
                override fun onResponse(
                    call: Call<List<Course>>,
                    response: Response<List<Course>>
                ) {
                    emiter.onNext(response.body() ?: listOf())
                    emiter.onComplete()
                }

                override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                    emiter.onError(t)
                    emiter.onComplete()
                }
            })
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun saveCourse(course: Course): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.CourseAPI::class.java)
            api.saveCourse(
                course.courseName, course.courseDescription,
                course.courseDateCreat, course.courseStatus
            )
                .enqueue(object : Callback<RestResponse> {
                    override fun onResponse(
                        call: Call<RestResponse>,
                        response: Response<RestResponse>
                    ) {
                        val body = response.body()
                        body?.let {
                            emiter.onNext(RestResponse(body.responseId, body.responseDescription))
                            emiter.onComplete()
                        } ?: run {
                            emiter.onError(CommonException(CommonException.HOST_RESP_BODY_NULL))
                            emiter.onComplete()
                        }
                    }

                    override fun onFailure(
                        call: Call<RestResponse>,
                        t: Throwable
                    ) {
                        emiter.onError(t)
                        emiter.onComplete()
                    }
                })
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun deleteCourse(id: Int): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.CourseAPI::class.java)
            api.deleteCourse(id).enqueue(object: Callback<RestResponse> {
                override fun onResponse(
                    call: Call<com.example.vocabmanager.entities.Response>,
                    response: Response<RestResponse>
                ) {
                    val body = response.body()
                    body?.let {
                        emiter.onNext(it)
                        emiter.onComplete()
                    } ?: run {
                        emiter.onError(CommonException(CommonException.HOST_RESP_BODY_NULL))
                        emiter.onComplete()
                    }
                }

                override fun onFailure(
                    call: Call<RestResponse>,
                    t: Throwable
                ) {
                    emiter.onError(t)
                }
            })
        }
    }

    fun updateCourse(course: Course): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.CourseAPI::class.java)
            api.updateCourse(
                course.courseId.toString(),
                course.courseName,
                course.courseDescription,
                course.courseDateCreat,
                course.courseStatus.toString()
            ).enqueue(object : Callback<RestResponse> {
                override fun onResponse(
                    call: Call<com.example.vocabmanager.entities.Response>,
                    response: Response<com.example.vocabmanager.entities.Response>
                ) {
                    val body = response.body()
                    body?.let {
                        emiter.onNext(it)
                        emiter.onComplete()
                    } ?: run {
                        emiter.onError(CommonException(CommonException.HOST_RESP_BODY_NULL))
                        emiter.onComplete()
                    }
                }

                override fun onFailure(
                    call: Call<com.example.vocabmanager.entities.Response>,
                    t: Throwable
                ) {
                    emiter.onError(t)
                    emiter.onComplete()
                }
            })
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
package com.example.vocabmanager.api.service

import android.content.Context
import com.example.vocabmanager.api.GlobalUtils
import com.example.vocabmanager.api.consts.CommonException
import com.example.vocabmanager.entities.Lesson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.vocabmanager.entities.Response as RestResponse

class LessonService(val context: Context) {
    fun getAllLessons(
        searchStr: String?,
        courseID: String?,
        statusID: String?
    ): Observable<List<Lesson>> {
        return Observable.create {
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.LessonAPI::class.java)
            api.listLesson(searchStr, courseID, statusID).enqueue(object : Callback<List<Lesson>> {
                override fun onResponse(
                    call: Call<List<Lesson>>,
                    response: Response<List<Lesson>>
                ) {
                    it.onNext(response.body() ?: listOf())
                    it.onComplete()
                }

                override fun onFailure(call: Call<List<Lesson>>, t: Throwable) {
                    it.onError(t)
                    it.onComplete()
                }
            })
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun saveLesson(lesson: Lesson): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.LessonAPI::class.java)
            api.saveLesson(lesson.lessonName, lesson.lessonCourse.toString(), lesson.lessonStatus)
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteLesson(id: Int): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.LessonAPI::class.java)
            api.deleteLesson(id).enqueue(object : Callback<RestResponse> {
                override fun onResponse(
                    call: Call<RestResponse>,
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
                    emiter.onComplete()
                }
            })
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateLesson(lesson: Lesson): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.LessonAPI::class.java)
            api.updateLesson(lesson.lessonId.toString(), lesson.lessonName,
                lesson.lessonCourse.toString(), lesson.lessonStatus).enqueue(object: Callback<RestResponse> {
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
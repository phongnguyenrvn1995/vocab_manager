package com.example.vocabmanager.api.service

import android.content.Context
import com.example.vocabmanager.api.GlobalUtils
import com.example.vocabmanager.api.consts.CommonException
import com.example.vocabmanager.entities.Vocab
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.vocabmanager.entities.Response as RestResponse

class VocabService(val context: Context) {
    companion object {
        const val LIMIT = 5
    }

    fun getAllVocab(
        searchStr: String?,
        typeID: String?,
        lessonID: String?,
        page: Int = 1
    ): Observable<List<Vocab>> {
        return Observable.create { emitter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.VocabAPI::class.java)
            var offset: Int = page * LIMIT - LIMIT
            offset = if (offset >= 0) offset else 0
            api.listVocab(
                searchStr = searchStr,
                typeID = typeID,
                lessonID = lessonID,
                limit = LIMIT,
                offset = offset
            ).enqueue(object : Callback<List<Vocab>> {
                override fun onResponse(
                    call: Call<List<Vocab>>,
                    response: Response<List<Vocab>>
                ) {
                    emitter.onNext(response.body() ?: listOf())
                    emitter.onComplete()
                }

                override fun onFailure(call: Call<List<Vocab>>, t: Throwable) {
                    emitter.onError(t)
                    emitter.onComplete()
                }
            })
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun saveVocab(vocab: Vocab): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.VocabAPI::class.java)
            api.saveVocab(
                vocab_type = vocab.vocabType,
                vocab_lesson = vocab.vocabLesson,
                vocab_en = vocab.vocabEn,
                vocab_ipa = vocab.vocabIpa,
                vocab_vi = vocab.vocabVi,
                vocab_description = vocab.vocabDescription,
                vocab_sound_url = vocab.vocabSoundUrl
            ).enqueue(object : Callback<RestResponse> {
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
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    fun updateVocab(vocab: Vocab): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.VocabAPI::class.java)
            api.updateVocab(
                vocab_id = vocab.vocabId,
                vocab_type = vocab.vocabType,
                vocab_lesson = vocab.vocabLesson,
                vocab_en = vocab.vocabEn,
                vocab_ipa = vocab.vocabIpa,
                vocab_vi = vocab.vocabVi,
                vocab_description = vocab.vocabDescription,
                vocab_sound_url = vocab.vocabSoundUrl
            ).enqueue(object : Callback<RestResponse> {
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

    fun deleteVocab(id: Int): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.VocabAPI::class.java)
            api.deleteVocab(id).enqueue(object : Callback<RestResponse> {
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
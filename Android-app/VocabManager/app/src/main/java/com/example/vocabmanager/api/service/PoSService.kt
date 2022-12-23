package com.example.vocabmanager.api.service

import android.content.Context
import com.example.vocabmanager.api.GlobalUtils
import com.example.vocabmanager.api.consts.CommonException
import com.example.vocabmanager.entities.PoS
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.vocabmanager.entities.Response as RestResponse

class PoSService(val context: Context) {
    fun getAllPoS(searchStr: String?): Observable<List<PoS>> {
        return Observable.create { emitter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.PoSAPI::class.java)
            api.listPoS(searchStr).enqueue(object : Callback<List<PoS>> {
                override fun onResponse(
                    call: Call<List<PoS>>,
                    response: Response<List<PoS>>
                ) {
                    emitter.onNext(response.body() ?: listOf())
                    emitter.onComplete()
                }

                override fun onFailure(call: Call<List<PoS>>, t: Throwable) {
                    emitter.onError(t)
                    emitter.onComplete()
                }
            })
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun savePoS(poS: PoS): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.PoSAPI::class.java)
            api.savePoS(poS.vocabTypeName).enqueue(object : Callback<RestResponse> {
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

    fun updatePoS(poS: PoS): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.PoSAPI::class.java)
            api.updatePoS(
                poS.vocabTypeId.toString(),
                poS.vocabTypeName
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

    fun deletePoS(id: Int): Observable<RestResponse> {
        return Observable.create { emiter ->
            val api = GlobalUtils.RETROFIT.create(GlobalUtils.PoSAPI::class.java)
            api.deletePoS(id).enqueue(object : Callback<RestResponse> {
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
}
package com.example.vocabmanager.api.service

import android.content.Context
import com.example.vocabmanager.R
import com.example.vocabmanager.entities.Status
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class StatusService(val context: Context) {
    fun getAllStatus(): Observable<List<Status>> {
        return Observable.create<List<Status>> { emitter ->
            val list = mutableListOf<Status>()
            list.add(Status(Status.ACTIVE, context.getString(R.string.data_status_active)))
            list.add(Status(Status.DE_ACTIVE, context.getString(R.string.data_status_de_active)))
            emitter.onNext(list)
            emitter.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
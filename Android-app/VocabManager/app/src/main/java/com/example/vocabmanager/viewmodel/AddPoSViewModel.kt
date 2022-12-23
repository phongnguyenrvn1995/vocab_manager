package com.example.vocabmanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.vocabmanager.Repository
import com.example.vocabmanager.entities.PoS
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.entities.validate.PoSValidate
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver

class AddPoSViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "AddPoSViewModel"
    }

    private var repository: Repository
    private var compositeDisposable: CompositeDisposable
    var poSValidateData: MutableLiveData<PoSValidate>
    var respData: MutableLiveData<Response>
    var errorData: MutableLiveData<Throwable>
    var poSData: MutableLiveData<PoS>

    init {
        repository = Repository(application)
        compositeDisposable = CompositeDisposable()
        poSValidateData = MutableLiveData()
        respData = MutableLiveData()
        errorData = MutableLiveData()
        poSData = MutableLiveData()
    }

    private fun validate(poS: PoS): Boolean {
        val validateObj = PoSValidate()
        if (poS.vocabTypeName == null || poS.vocabTypeName?.trim()?.isEmpty() == true)
            validateObj.isNamePassed = false

        poSValidateData.postValue(validateObj)
        return validateObj.isPassed()
    }


    fun savePoS(posName: String) {
        val poS = PoS(
            vocabTypeName = posName
        )

        if (!validate(poS)) {
            return
        }

        val d = repository.addPoS(poS)
            .subscribeWith(object : DisposableObserver<Response>() {
                override fun onNext(t: Response) {
                    Log.d(TAG, "onNext: $t")
                    respData.postValue(t)
                    if (t.responseId == Response.SUCCESS) {
                        poSData.postValue(poS)
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
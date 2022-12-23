package com.example.vocabmanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.vocabmanager.Repository
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.entities.Vocab
import com.example.vocabmanager.entities.validate.VocabValidate
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver

class UpdateVocabViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "UpdateVocabViewModel"
    }

    private var repository: Repository
    private var compositeDisposable: CompositeDisposable
    var vocabValidateData: MutableLiveData<VocabValidate>
    var respData: MutableLiveData<Response>
    var errorData: MutableLiveData<Throwable>
    var vocabData: MutableLiveData<Vocab>

    init {
        repository = Repository(application)
        compositeDisposable = CompositeDisposable()
        vocabValidateData = MutableLiveData()
        respData = MutableLiveData()
        errorData = MutableLiveData()
        vocabData = MutableLiveData()
    }

    private fun validate(vocab: Vocab): Boolean {
        val validateObj = VocabValidate()
        if (vocab.vocabEn == null || vocab.vocabEn?.trim()?.isEmpty() == true)
            validateObj.isEnPassed = false
        if (vocab.vocabIpa == null || vocab.vocabIpa?.isEmpty() == true)
            validateObj.isIpaPassed = false
        if (vocab.vocabDescription == null || vocab.vocabDescription?.isEmpty() == true)
            validateObj.isDescPassed = false
        if (vocab.vocabVi == null || vocab.vocabVi?.isEmpty() == true)
            validateObj.isViPassed = false/*
        if (vocab.vocabSoundUrl == null || vocab.vocabSoundUrl?.isEmpty() == true)
            validateObj.isSoundPassed = false*/
        if (vocab.vocabType == null || vocab.vocabType?.toString()?.isEmpty() == true)
            validateObj.isPOSPassed = false
        if (vocab.vocabLesson == null || vocab.vocabLesson?.toString()?.isEmpty() == true)
            validateObj.isLessonPassed = false

        vocabValidateData.postValue(validateObj)
        return validateObj.isPassed()
    }

    fun updateVocab(vocabId: Int?,
        vocabEn: String, vocabIpa: String, vocabDescription: String,
        vocabVi: String, vocabSoundUrl: String, vocabType: Int, vocabLesson: Int
    ) {
        val vocab = Vocab(
            vocabId = vocabId,
            vocabEn = vocabEn,
            vocabIpa = vocabIpa,
            vocabDescription = vocabDescription,
            vocabVi = vocabVi,
            vocabSoundUrl = vocabSoundUrl,
            vocabType = vocabType,
            vocabLesson = vocabLesson
        )

        if (!validate(vocab)) {
            return
        }

        val d = repository.updateVocab(vocab)
            .subscribeWith(object : DisposableObserver<Response>() {
                override fun onNext(t: Response) {
                    Log.d(TAG, "onNext: $t")
                    respData.postValue(t)
                    if (t.responseId == Response.SUCCESS) {
                        vocabData.postValue(vocab)
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
package com.example.vocabmanager

import android.app.Application

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupRetrofit()
    }

    private fun setupRetrofit() {
    }
}
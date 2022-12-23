package com.example.vocabmanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vocabmanager.databinding.ActivitySplashBinding
import io.reactivex.rxjava3.core.Completable
import java.util.concurrent.TimeUnit

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        showMainScreen()
    }

    private fun showMainScreen() {
        Completable.complete()
            .delay(3, TimeUnit.SECONDS)
            .subscribe {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }
}
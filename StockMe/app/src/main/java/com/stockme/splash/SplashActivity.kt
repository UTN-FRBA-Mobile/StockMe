package com.stockme.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.stockme.R
import com.stockme.home.HomeActivity
import com.stockme.welcome.WelcomeActivity_
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(R.layout.activity_splash), CoroutineScope {
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch {
            delay(2000L)
            if (auth.currentUser != null) {

                val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@SplashActivity, WelcomeActivity_::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()
}
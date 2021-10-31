package com.stockme.login

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.stockme.BuildConfig
import com.stockme.home.MainActivity_
import com.stockme.Prefs_
import com.stockme.R
import com.stockme.buy.BuyActivity
import com.stockme.databinding.ActivityLoginBinding
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.sharedpreferences.Pref


@EActivity(R.layout.activity_login)
class LoginActivity : AppCompatActivity() {

    @Pref
    protected lateinit var prefs: Prefs_

    lateinit var binding: ActivityLoginBinding

    @AfterViews
    fun start() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        binding.version.text = BuildConfig.VERSION_NAME

        binding.root.postDelayed(Runnable {
            if (prefs.loggedIn().get()) {
                MainActivity_.intent(this).start()
                finish()
            } else {
                binding.topSpace.visibility = View.GONE

                binding.root.postDelayed({
                    binding.buttonsContainer.visibility = View.VISIBLE
                }, 500)

                binding.root.postDelayed({
                    binding.isologo.visibility = View.VISIBLE
                }, 400)
            }
        }, 1200)
    }

    @Click
    fun login() {
        val intent = Intent(this, BuyActivity::class.java)
        startActivity(intent)
    }
}
package com.stockme.welcome

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.stockme.BuildConfig
import com.stockme.home.MainActivity_
import com.stockme.Prefs_
import com.stockme.R
import com.stockme.databinding.ActivityWelcomeBinding
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.sharedpreferences.Pref


@EActivity(R.layout.activity_welcome)
class WelcomeActivity : AppCompatActivity() {

    @Pref
    protected lateinit var prefs: Prefs_

    lateinit var binding: ActivityWelcomeBinding

    @AfterViews
    fun start() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)

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
        binding.buttonsContainer.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        // FIX(Mati): postDelated is not working for me now. I had a couple of
        // nice animations set up but for some reason it stopped working. Will check it out.
//        binding.root.postDelayed(Runnable {
            prefs.loggedIn().put(true)
            MainActivity_.intent(this).start()
            finish()
//        }, 2000)
    }

    @Click
    fun register() {

    }
}
package com.stockme.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.stockme.R
import com.stockme.databinding.ActivityRegisterBinding
import com.stockme.home.HomeActivity
import com.stockme.register.viewmodel.RegisterViewModel
import com.stockme.utils.hideProgress
import com.stockme.utils.showProgress
import com.stockme.utils.showSnackBar

class RegisterActivity : AppCompatActivity() {
    private val TAG = "RegisterActivity"

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel = RegisterViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupObserver()
    }

    private fun setupViews() {
        binding.registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun setupObserver() {
        viewModel.signUpLiveData.observe(this) {
            hideProgress()
            if (it) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }
                    val token = task.result

                    Log.d(TAG, token!!)

                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                })

            } else {
                showSnackBar(binding.root, R.string.register_error)
            }
        }
    }

    private fun registerUser() {
        if (areFieldsValid()) {
            showProgress()
            viewModel.registerUser(
                binding.registerEmailEditText.text.toString(),
                binding.registerPasswordEditText.text.toString()
            )
        } else {
            if (!isValidEmail(binding.registerEmailEditText.text)) {
                showSnackBar(binding.root, R.string.login_invalid_email)

            }  else {
                showSnackBar(binding.root, R.string.login_invalid_parameters)
            }
        }
    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    private fun areFieldsValid(): Boolean = !binding.registerNameEditText.text.trim().isNullOrBlank()
            && !binding.registerEmailEditText.text.trim().isNullOrBlank()
            && !binding.registerPasswordEditText.text.trim().isNullOrBlank()
            && !binding.registerRepeatPasswordEditText.text.trim().isNullOrBlank()
            && binding.registerPasswordEditText.text.toString() == binding.registerRepeatPasswordEditText.text.toString()
            && binding.registerCheckbox.isChecked
}
package com.stockme.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.stockme.R
import com.stockme.databinding.ActivityLoginBinding
import com.stockme.home.HomeActivity
import com.stockme.login.viewmodel.LoginViewModel
import com.stockme.register.RegisterActivity
import com.stockme.utils.hideProgress
import com.stockme.utils.showProgress
import com.stockme.utils.showSnackBar
import android.text.TextUtils
import android.util.Patterns


class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var binding: ActivityLoginBinding
    private val viewModel = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.forgotPasswordText.setOnClickListener {
            showResetPasswordDialog()
        }

        binding.noAccountText.setOnClickListener {
            registerUser()
        }

        binding.loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun setupObservers() {
        viewModel.signInLiveData.observe(this) {
            hideProgress()
            if (it) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                showSnackBar(binding.root, R.string.login_error)
            }
        }

        viewModel.passwordResetLiveData.observe(this) {
            hideProgress()
            if (it) {
                showDialog()
            } else {
                showSnackBar(binding.root, R.string.login_error)
            }
        }
    }

    private fun showResetPasswordDialog() {
        val type = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        MaterialDialog(this).show {
            title(R.string.login_forgot_password_dialog_title)
            input(inputType = type)
            positiveButton(R.string.login_forgot_password_dialog_positive_cta) {
                if (isValidEmail(it.getInputField().text.toString()))
                {
                resetPassword(it.getInputField().text.toString())
                } else{
                    showSnackBar(binding.root, R.string.login_invalid_email)
                }
            }
            negativeButton(R.string.login_forgot_password_dialog_negative_cta)
        }
    }

    private fun loginUser() {
        if (areLoginFieldsValid()) {
            showProgress()
            viewModel.loginUser(binding.loginEmailEditText.text.toString(), binding.loginPasswordEditText.text.toString()).addOnCompleteListener {

                if (!it.isSuccessful) {
                    showSnackBar(binding.root, R.string.login_invalid_user_password)
                    return@addOnCompleteListener
                }
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

            }
        } else {
            if (!isValidEmail(binding.loginEmailEditText.text)) {
                showSnackBar(binding.root, R.string.login_invalid_email)

            }  else {
                showSnackBar(binding.root, R.string.login_invalid_parameters)
            }
        }
    }

    private fun registerUser() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    private fun resetPassword(email: String) {
        if (email.trim().isNotBlank()) {
            showProgress()
            viewModel.resetPassword(email)
        } else {
            if (!isValidEmail(binding.loginEmailEditText.text)) {
                showSnackBar(binding.root, R.string.login_invalid_email)

            }  else {
                showSnackBar(binding.root, R.string.login_invalid_parameters)
            }
        }
    }

    private fun areLoginFieldsValid(): Boolean = isValidEmail(binding.loginEmailEditText.text) && !binding.loginPasswordEditText.text.isNullOrBlank()

    private fun showDialog() {
        MaterialDialog(this).show {
            title(R.string.login_password_reset_successful)
            positiveButton(R.string.login_forgot_password_dialog_positive_cta)
        }
    }
}
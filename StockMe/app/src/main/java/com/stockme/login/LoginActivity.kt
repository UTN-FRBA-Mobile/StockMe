package com.stockme.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.stockme.R
import com.stockme.databinding.ActivityLoginBinding
import com.stockme.home.HomeActivity
import com.stockme.login.viewmodel.LoginViewModel
import com.stockme.register.RegisterActivity
import com.stockme.utils.hideProgress
import com.stockme.utils.showProgress
import com.stockme.utils.showSnackBar

class LoginActivity : AppCompatActivity() {
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
                resetPassword(it.getInputField().text.toString())
            }

            negativeButton(R.string.login_forgot_password_dialog_negative_cta)
        }
    }

    private fun loginUser() {
        if (areLoginFieldsValid()) {
            showProgress()
            viewModel.loginUser(binding.loginEmailEditText.text.toString(), binding.loginPasswordEditText.text.toString()).addOnCompleteListener {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        } else {
            showSnackBar(binding.root, R.string.login_invalid_parameters)
        }
    }

    private fun registerUser() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun resetPassword(email: String) {
        if (email.trim().isNotBlank()) {
            showProgress()
            viewModel.resetPassword(email)
        } else {
            showSnackBar(binding.root, R.string.login_invalid_parameters)
        }
    }

    private fun areLoginFieldsValid(): Boolean = !binding.loginEmailEditText.text.isNullOrBlank() && !binding.loginPasswordEditText.text.isNullOrBlank()

    private fun showDialog() {
        MaterialDialog(this).show {
            title(R.string.login_password_reset_successful)
            positiveButton(R.string.login_forgot_password_dialog_positive_cta)
        }
    }
}
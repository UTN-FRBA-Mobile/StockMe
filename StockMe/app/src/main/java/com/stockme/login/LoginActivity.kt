package com.stockme.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.stockme.R
import com.stockme.databinding.ActivityLoginBinding
import com.stockme.home.MainActivity_
import com.stockme.login.viewmodel.LoginViewModel
import com.stockme.register.RegisterActivity
import com.stockme.utils.hideProgress
import com.stockme.utils.showProgress

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
                MainActivity_.intent(this).start()
                finish()
            } else {
                showSnackBar(getString(R.string.login_error))
            }
        }

        viewModel.passwordResetLiveData.observe(this) {
            hideProgress()
            if (it) {
                showDialog()
            } else {
                showSnackBar(getString(R.string.login_error))
            }
        }
    }

    private fun showResetPasswordDialog() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        val alertDialogB = AlertDialog.Builder(this)

        alertDialogB.apply {
            setTitle(getString(R.string.login_forgot_password_dialog_title))
            setView(input)
            setPositiveButton(getString(R.string.login_forgot_password_dialog_positive_cta)) { _, _ ->
                resetPassword(input.text.toString())
            }
            setNegativeButton(getString(R.string.login_forgot_password_dialog_negative_cta)) { dialog, _ ->
                dialog.cancel()
            }
        }.create().show()
    }

    private fun loginUser() {
        if (areLoginFieldsValid()) {
            showProgress()
            viewModel.loginUser(binding.loginEmailEditText.text.toString(), binding.loginPasswordEditText.text.toString())
        } else {
            showSnackBar(getString(R.string.login_invalid_parameters))
        }
    }

    private fun registerUser() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun resetPassword(email: String) {
        if (!email.trim().isNullOrBlank()) {
            showProgress()
            viewModel.resetPassword(email)
        } else {
            showSnackBar(getString(R.string.login_invalid_parameters))
        }
    }

    private fun areLoginFieldsValid(): Boolean = !binding.loginEmailEditText.text.isNullOrBlank() && !binding.loginPasswordEditText.text.isNullOrBlank()

    private fun showSnackBar(text: String) = Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.login_password_reset_successful))
        builder.setPositiveButton(getString(R.string.login_forgot_password_dialog_positive_cta)) { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}
package com.stockme.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.stockme.R
import com.stockme.databinding.ActivityLoginBinding
import com.stockme.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        binding.forgotPasswordText.setOnClickListener {
            // Show dialog
        }

        binding.noAccountText.setOnClickListener {
            registerUser()
        }

        binding.loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        if (areFieldsValid()) {

        } else {
            showSnackBar(getString(R.string.login_invalid_parameters))
        }
    }

    private fun registerUser() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun areFieldsValid(): Boolean = !binding.loginEmailEditText.text.isNullOrBlank() && !binding.loginPasswordEditText.text.isNullOrBlank()

    private fun showSnackBar(text: String) = Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
}
package com.stockme.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.stockme.R
import com.stockme.databinding.ActivityRegisterBinding
import com.stockme.home.MainActivity_
import com.stockme.register.viewmodel.RegisterViewModel
import com.stockme.utils.hideProgress
import com.stockme.utils.showProgress

class RegisterActivity : AppCompatActivity() {
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
            showProgress()
            registerUser()
        }
    }

    private fun setupObserver() {
        viewModel.signUpLiveData.observe(this) {
            hideProgress()
            if (it) {
                MainActivity_.intent(this).start()
                finish()
            } else {
                showSnackBar(getString(R.string.register_error))
            }
        }
    }

    private fun registerUser() {
        if (areFieldsValid()) {
            viewModel.registerUser(
                binding.registerEmailEditText.text.toString(),
                binding.registerPasswordEditText.text.toString()
            )
        } else {
            showSnackBar(getString(R.string.register_invalid_parameters))
        }
    }

    private fun areFieldsValid(): Boolean = !binding.registerNameEditText.text.trim().isNullOrBlank()
            && !binding.registerEmailEditText.text.trim().isNullOrBlank()
            && !binding.registerPasswordEditText.text.trim().isNullOrBlank()
            && !binding.registerRepeatPasswordEditText.text.trim().isNullOrBlank()
            && binding.registerPasswordEditText.text.toString() == binding.registerRepeatPasswordEditText.text.toString()
            && binding.registerCheckbox.isChecked

    private fun showSnackBar(text: String) = Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
}
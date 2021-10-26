package com.stockme.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.stockme.R
import com.stockme.databinding.ActivityRegisterBinding
import org.androidannotations.annotations.EActivity

@EActivity(R.layout.activity_register)
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        binding.registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        if (areFieldsValid()) {
//            viewModel.registerUser(
//                binding.registerNameEditText.text.toString(),
//                binding.registerEmailEditText.text.toString(),
//                binding.registerPasswordEditText.text.toString()
//            )
        } else {
            showSnackBar(getString(R.string.register_invalid_parameters))
        }
    }

    private fun areFieldsValid(): Boolean = !binding.registerNameEditText.text.isNullOrBlank()
            && !binding.registerEmailEditText.text.isNullOrBlank()
            && !binding.registerPasswordEditText.text.isNullOrBlank()
            && !binding.registerRepeatPasswordEditText.text.isNullOrBlank()
            && binding.registerPasswordEditText.text.toString() == binding.registerRepeatPasswordEditText.text.toString()
            && binding.registerCheckbox.isActivated

    private fun showSnackBar(text: String) = Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
}
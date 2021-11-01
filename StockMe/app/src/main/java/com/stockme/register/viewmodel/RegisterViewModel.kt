package com.stockme.register.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterViewModel: ViewModel() {
    private val _signUpLiveData = MutableLiveData<Boolean>()
    private val auth: FirebaseAuth = Firebase.auth

    val signUpLiveData: MutableLiveData<Boolean> get() = _signUpLiveData

    fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                _signUpLiveData.value = it.isSuccessful
            }
    }
}
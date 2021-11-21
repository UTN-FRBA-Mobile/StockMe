package com.stockme.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _signInLiveData = MutableLiveData<Boolean>()
    private val _passwordResetLiveData = MutableLiveData<Boolean>()

    val signInLiveData: MutableLiveData<Boolean> get() = _signInLiveData
    val passwordResetLiveData: MutableLiveData<Boolean> get() = _passwordResetLiveData

    fun loginUser(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            _signInLiveData.value = it.isSuccessful
        }
    }

    fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            _passwordResetLiveData.value = it.isSuccessful
        }
    }
}
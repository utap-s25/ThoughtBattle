package com.example.thoughtbattle.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth

    val currentUser: MutableLiveData<FirebaseUser?> = MutableLiveData()

    init {
        currentUser.value = auth.currentUser
    }

    fun setCurrentUser(user: FirebaseUser?) {
        currentUser.value = user
    }

    fun signOut() {
        auth.signOut()
        currentUser.value = null
    }
}
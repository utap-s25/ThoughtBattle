package com.example.thoughtbattle.ui.auth

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.thoughtbattle.R
import com.example.thoughtbattle.data.model.User
import com.example.thoughtbattle.data.model.invalidUser
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthUser(private val registry: ActivityResultRegistry) :
    DefaultLifecycleObserver, FirebaseAuth.AuthStateListener {
    companion object {
        private const val TAG = "AuthUser"
    }

    private var pendingLogin = false
    private lateinit var auth: FirebaseAuth

    //basically just stealign the Auth from our flipped classrooms lololol
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    //so when we initially start our app and user is new, it takes them to sign in :)
    private var liveUser = MutableLiveData<User>().apply {
        this.postValue(invalidUser)
    }

    init {
        auth = Firebase.auth
       auth.addAuthStateListener(this)
    }

    fun observeUser(): LiveData<User> {
        return liveUser
    }

    private fun postUserUpdate(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null) {
            Log.d(TAG, "postUser login")
            liveUser.postValue(invalidUser)
            login()
        } else {
            val user = User(
                firebaseUser.uid,
                firebaseUser.displayName ?: "",
                firebaseUser.email ?: "",
                firebaseUser.photoUrl.toString()
            )

            liveUser.postValue(user)
        }


    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        postUserUpdate(auth.currentUser)

    }

    override fun onCreate(owner: LifecycleOwner) {
        signInLauncher =
            registry.register("key", owner, FirebaseAuthUIActivityResultContract()) { result ->
                pendingLogin = false

            }
    }

    private fun user(): FirebaseUser? {
        return auth.currentUser
    }
    //@ bryan looking at fc7, maybe you can functionality for profile here?? idk


    fun login() {
        if (user() == null && !pendingLogin) {
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers).setLogo(R.drawable.thoughtbatle)
                .setIsSmartLockEnabled(false)
                .setTheme(R.style.Theme_FirebaseAuth_NoActionBar)
                .build()
            signInLauncher.launch(signInIntent)
        }

    }

    fun logout() {
        if (user() == null) return
        auth.signOut()
        login()
    }
}

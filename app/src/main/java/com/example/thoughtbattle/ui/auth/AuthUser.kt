package com.example.thoughtbattle.ui.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.example.thoughtbattle.data.repository.FirebaseRepository
import com.example.thoughtbattle.data.repository.SendBirdRepository
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sendbird.android.SendbirdChat
import com.sendbird.android.params.UserUpdateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthUser(private val registry: ActivityResultRegistry) :
    DefaultLifecycleObserver, FirebaseAuth.AuthStateListener {
    companion object {
        private const val TAG = "AuthUser"
    }


    private var pendingLogin = false
    private var userPosted = false



    //basically just stealign the Auth from our flipped classrooms lololol
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    //so when we initially start our app and user is new, it takes them to sign in :)
    private var liveUser = MutableLiveData<User>().apply {
        this.postValue(invalidUser)
    }

    init {

        Firebase.auth.addAuthStateListener(this)
    }

    fun observeUser(): LiveData<User> {
        if (!userPosted) {
            userPosted = true
            return liveUser
        }
        return MutableLiveData<User>()
    }


    private  fun postUserUpdate(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null || firebaseUser.uid == null || firebaseUser.uid =="null") {
            liveUser.postValue(invalidUser)
            login()
        } else {



                        val user = User(
                            id = firebaseUser.uid,
                            username = firebaseUser.displayName ?: "New User",
                            profileImageUrl = firebaseUser.photoUrl?.toString() ?: ""
                        )
userPosted=true





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
                if (result.resultCode == Activity.RESULT_OK) {

                    val user = Firebase.auth.currentUser
                    // SendBirdRepository.connect(user!!.uid)

                    Log.d(TAG, "Sign-in successful: ${user?.email}")
                } else {

                    Log.d(TAG, "Sign-in failed: ${result.idpResponse?.error?.errorCode}")
                }
            }
    }

    private fun user(): FirebaseUser? {
        return Firebase.auth.currentUser
    }


    fun login() {
        if (user() == null && !pendingLogin) {
            pendingLogin = true
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


    suspend fun updateProfile(
        displayName: String,
        photoUri: Uri?,
        onComplete: (Exception?) -> Unit
    ) {

        try {

            FirebaseRepository.updateUserProfile(displayName, photoUri)


            val updates = hashMapOf<String, Any>(
                "displayName" to displayName
            )
            photoUri?.let { updates["photoUrl"] = it.toString() }

            FirebaseRepository.updateFirestoreUser(updates)


            val user = liveUser.value?.let { FirebaseRepository.getFirestoreUser(it.id) }

            SendbirdChat.updateCurrentUserInfo(UserUpdateParams().apply {
                nickname = user?.username
                profileImageUrl = user?.profileImageUrl
            }) {
                if (it != null) {
                    Log.e(TAG, "Error updating Sendbird user info", it)
                }
            }

            liveUser.postValue(user)

            onComplete(null)
        } catch (e: Exception) {
            onComplete(e)
        }
    }


    suspend fun loadUserProfile(uid: String): User {
        var userProfile = User()
        try {
            userProfile = FirebaseRepository.getFirestoreUser(uid)

        } catch (e: Exception) {
            Log.e(TAG, "Error loading user profile", e)
        }
        return userProfile

    }

    fun logout() {
        if (user() == null) return
        Firebase.auth.signOut()
        login()
    }
}

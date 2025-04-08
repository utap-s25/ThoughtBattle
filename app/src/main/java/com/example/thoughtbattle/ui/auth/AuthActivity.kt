package com.example.thoughtbattle.ui.auth
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thoughtbattle.data.model.User
import com.example.thoughtbattle.databinding.ActivityAuthBinding
import com.example.thoughtbattle.ui.auth.AuthViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sendbird.android.SendbirdChat
import androidx.activity.viewModels
import com.example.thoughtbattle.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AuthActivity", "onCreate() called")

        binding = ActivityAuthBinding.inflate(layoutInflater)
        Log.d("AuthActivity", "binding inflated")

        setContentView(binding.root)
        Log.d("AuthActivity", "setContentView() called")


        Log.d("AuthActivity", "Checking if user is already signed in")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        viewModel.setCurrentUser(firebaseUser)
        if (firebaseUser != null) {
            Log.d("AuthActivity", "User is already signed in, starting MainActivity")
            startMainActivity()
            return
        }
        Log.d("AuthActivity", "User is not signed in")

        Log.d("AuthActivity", "Setting up signInButton listener")
        binding.signInButton.setOnClickListener {
            Log.d("AuthActivity", "signInButton clicked")
            startSignIn()
        }
        Log.d("AuthActivity", "signInButton listener set")

        Log.d("AuthActivity", "onCreate() finished")
    }

    private fun startSignIn() {
        Log.d("AuthActivity", "startSignIn() called")
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        Log.d("AuthActivity", "Starting sign-in activity")
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
        Log.d("AuthActivity", "startSignIn() finished")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("AuthActivity", "onActivityResult() called with requestCode: $requestCode, resultCode: $resultCode")
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Log.d("AuthActivity", "Sign in successful")
                // Successfully signed in
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                viewModel.setCurrentUser(firebaseUser)
                createUserInFirestore(firebaseUser)
                createUserInSendBird(firebaseUser)
                startMainActivity()
            } else {
                Log.w("AuthActivity", "Sign in failed")

                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
        Log.d("AuthActivity", "onActivityResult() finished")
    }

    private fun createUserInFirestore(firebaseUser: FirebaseUser?) {
        Log.d("AuthActivity", "createUserInFirestore() called")
        val user = firebaseUser ?: run {
            Log.e("AuthActivity", "currentUser is null in createUserInFirestore()")
            return
        }

        val firestoreUser = User(
            id = user.uid,
            email = user.email ?: "",
            username = user.displayName ?: user.email?.substringBefore("@") ?: "user_${user.uid.take(4)}"
        )

        Log.d("AuthActivity", "Creating user in Firestore: ${firestoreUser.id}")
        Firebase.firestore.collection("users").document(user.uid)
            .set(firestoreUser)
            .addOnFailureListener { e ->
                Log.e("AuthActivity", "Error creating user in Firestore", e)
            }
        Log.d("AuthActivity", "createUserInFirestore() finished")
    }

    private fun createUserInSendBird(firebaseUser: FirebaseUser?) {
        Log.d("AuthActivity", "createUserInSendBird() called")
        val user = firebaseUser ?: run {
            Log.e("AuthActivity", "currentUser is null in createUserInSendBird()")
            return
        }

        Log.d("AuthActivity", "Connecting to SendBird with user ID: ${user.uid}")
        SendbirdChat.connect(user.uid) { sendbirdUser, e ->
            if (e != null) {
                Log.e("AuthActivity", "SendBird connection failed", e)
                return@connect
            }
            Log.d("AuthActivity", "SendBird connection successful")
        }
        Log.d("AuthActivity", "createUserInSendBird() finished")
    }

    private fun startMainActivity() {
        Log.d("AuthActivity", "startMainActivity() called")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        Log.d("AuthActivity", "startMainActivity() finished")
    }

    companion object {
        private const val RC_SIGN_IN = 1001
    }
}
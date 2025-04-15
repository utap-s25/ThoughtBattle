package com.example.thoughtbattle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.thoughtbattle.data.repository.SendBirdRepository
import com.example.thoughtbattle.databinding.ActivityMainBinding
import com.example.thoughtbattle.ui.MainViewModel
import com.example.thoughtbattle.ui.auth.AuthUser
import com.example.thoughtbattle.ui.main.HomeActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var authUser: AuthUser
    private val viewModel: MainViewModel by viewModels()

    override fun onStart() {
        super.onStart()

        authUser = AuthUser(activityResultRegistry)
        lifecycle.addObserver(authUser)

        authUser.observeUser().observe(this) { user ->
            if (user == null) {
                // Handle the case where the user is null (e.g., not logged in)
                return@observe
            }
            // Save the user ID to SharedPreferences
            getSharedPreferences("sendbird", MODE_PRIVATE).edit().putString("user_id", user.id).apply()
            FirebaseApp.initializeApp(this)
            // Initialize Sendbird
            SendBirdRepository.initialize(this)
            viewModel.setCurrentAuthUser(user)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            Log.d("MainActivity", "AuthUser is observed")
            Log.d("MainActivity", "User is $user")
            finish()
        }
    }
}
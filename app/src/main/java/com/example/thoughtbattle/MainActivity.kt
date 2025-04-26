package com.example.thoughtbattle

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.thoughtbattle.data.model.isInvalid
import com.example.thoughtbattle.databinding.ActivityMainBinding
import com.example.thoughtbattle.ui.MainViewModel
import com.example.thoughtbattle.ui.auth.AuthUser
import com.example.thoughtbattle.ui.main.DebateListFragment
import com.sendbird.android.SendbirdChat


class MainActivity : AppCompatActivity() {
        private lateinit var binding: ActivityMainBinding
        private lateinit var authUser: AuthUser
        private val viewModel: MainViewModel by viewModels()
        private lateinit var navController: NavController

    private fun initToolbar() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_create_channel -> {
                        navController.navigate(R.id.createDebateFragment)
                        true
                    }

                    R.id.action_home -> {
                        navController.navigate(R.id.homeFragment)
                        true
                    }

                    R.id.action_search -> {
                        // Handle search
                        true
                    }

                    R.id.action_personal -> {
                        // Handle personal
                        true
                    }

                    R.id.action_settings -> {
                        // Handle settings
                        true
                    }

                    R.id.action_sign_out -> {
                        authUser.logout()
                        true
                    }

                    R.id.action_profile -> {
                        viewModel.currentAuthUser.value?.let{ user ->
                            val userId = user.id
                            Log.d("MainActivity", "User ID: $userId")
                            if (userId != null && userId != "-1") {
                                val bundle = Bundle()
                                bundle.putString("PROFILE_USER_ID", userId)
                                viewModel.setUserId(userId)
                                navController.navigate(R.id.action_home_to_profile, bundle)
                            }
                        }
                        true
                    }

                    else -> false
                }
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.addToolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Update this line to use the correct navigation graph
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment,R.id.createDebateFragment,R.id.debateFragment,R.id.debateSettingsFragment,R.id.profileFragment,R.id.editProfileFragment)

        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        initToolbar()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }




    override fun onStart() {
        super.onStart()

        authUser = AuthUser(activityResultRegistry)
        lifecycle.addObserver(authUser)

        authUser.observeUser().observe(this) { user ->
            if (user.isInvalid()) {
                authUser.login()
                return@observe
            }

            getSharedPreferences("sendbird", MODE_PRIVATE).edit()
                .putString("user_id", user.id).apply()
            getSharedPreferences("sendbird", MODE_PRIVATE).edit()
                .putString("user_nickname", user.username).apply()
            getSharedPreferences("sendbird", MODE_PRIVATE).edit()
                .putString("user_profile_pic", user.profileImageUrl).apply()

            if(user.id != null || user.id != "-1" || user.id !="null" || user.id != " ") {
                viewModel.setCurrentAuthUser(user)
                viewModel.setUserId(user.id)
            }
        }
    }
        }


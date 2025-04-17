package com.example.thoughtbattle

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.thoughtbattle.data.model.isInvalid
import com.example.thoughtbattle.databinding.ActivityMainBinding
import com.example.thoughtbattle.ui.MainViewModel
import com.example.thoughtbattle.ui.auth.AuthUser


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

                            true
                        }

                        R.id.action_settings -> {

                            true
                        }

                        R.id.action_sign_out -> {
                            authUser.logout()
                            true
                        }

                        R.id.action_profile -> {

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
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            val appBarConfiguration = AppBarConfiguration(navController.graph)
            setupActionBarWithNavController(navController, appBarConfiguration)
            initToolbar()


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

                getSharedPreferences("sendbird", MODE_PRIVATE).edit().putString("user_id", user.id)
                    .apply()
                getSharedPreferences("sendbird", MODE_PRIVATE).edit().putString("user_nickname", user.username)
                    .apply()
                getSharedPreferences("sendbird", MODE_PRIVATE).edit().putString("user_profile_pic", user.profileImageUrl)
                    .apply()

                viewModel.setCurrentAuthUser(user)



            }
        }
    }

package com.example.thoughtbattle.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import com.example.thoughtbattle.R
import com.example.thoughtbattle.ui.MainViewModel
import com.sendbird.uikit.consts.StringSet
import com.sendbird.uikit.fragments.OpenChannelListFragment
import com.sendbird.uikit.modules.OpenChannelListModule

import androidx.activity.viewModels
import androidx.navigation.NavController
import com.example.thoughtbattle.MainActivity
import com.sendbird.uikit.modules.components.ChannelSettingsMenuComponent

/**
 * Displays an open channel list screen used for community.
 */
class DebateListFragment : OpenChannelListFragment() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

    }



/*

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_create_channel && context != null) {
            val intent = Intent(context, CreateDebateActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }*/


    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }
}
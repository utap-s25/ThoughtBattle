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
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.thoughtbattle.MainActivity
import com.sendbird.android.channel.OpenChannel
import com.sendbird.uikit.modules.components.ChannelSettingsMenuComponent

/**
 * Displays an open channel list screen used for community.
 */
class DebateListFragment : OpenChannelListFragment() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController






    override fun onItemClicked(view: View, position: Int, channel: OpenChannel) {
       // findNavController().navigate(R.id.action_debate_chat, bundleOf("KEY_CHANNEL_URL" to channel.url))
        val bundle = bundleOf("KEY_CHANNEL_URL" to channel.url)
        findNavController().navigate(R.id.action_home_to_debate_chat, bundle)
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }
}
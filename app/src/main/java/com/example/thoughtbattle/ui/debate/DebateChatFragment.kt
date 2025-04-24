package com.example.thoughtbattle.ui.debate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.thoughtbattle.data.model.Debate
import com.example.thoughtbattle.ui.MainViewModel
import com.example.thoughtbattle.ui.debate.information.DebateChatHeader
import com.sendbird.android.channel.OpenChannel
import com.sendbird.uikit.fragments.ChannelFragment
import com.sendbird.uikit.fragments.OpenChannelFragment
import com.sendbird.uikit.modules.components.OpenChannelHeaderComponent
import com.sendbird.uikit.vm.OpenChannelViewModel



class DebateChatFragment : ChannelFragment() {
    private val viewModel: MainViewModel by viewModels()

    private var pendingDebate: Debate? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        arguments?.getString("KEY_CHANNEL_URL")?.let { url ->
            viewModel.setCurrentDebateUrl(url)
        }
        return view
    }



}
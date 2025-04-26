package com.example.thoughtbattle.ui.debate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thoughtbattle.R
import com.example.thoughtbattle.data.model.Debate
import com.example.thoughtbattle.ui.MainViewModel
import com.example.thoughtbattle.ui.debate.information.DebateChatHeader
import com.sendbird.android.channel.OpenChannel
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.user.User
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

    override fun onMessageProfileClicked(view: View, position: Int, message: BaseMessage) {
        val args = Bundle()
        args.putString("PROFILE_USER_ID", message.sender?.userId)

        findNavController().navigate(R.id.action_debateChatFragment_to_userProfileFragment)
    }

    override fun onMessageMentionClicked(view: View, position: Int, user: User) {
        val args = Bundle()
        args.putString("PROFILE_USER_ID", user.userId)
        findNavController().navigate(R.id.action_debateChatFragment_to_userProfileFragment)

    }


}
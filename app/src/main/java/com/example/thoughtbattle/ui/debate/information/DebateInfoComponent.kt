package com.example.thoughtbattle.ui.debate.information

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thoughtbattle.R
import com.example.thoughtbattle.databinding.ViewCustomHeaderBinding
import com.example.thoughtbattle.databinding.ViewCustomInformationBinding
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.channel.OpenChannel
import com.sendbird.android.handler.MetaDataHandler
import com.sendbird.uikit.modules.components.ChannelSettingsInfoComponent
import com.sendbird.uikit.modules.components.OpenChannelSettingsInfoComponent


class DebateInfoComponent(private val fragment: DebateSettingsFragment) : ChannelSettingsInfoComponent() {

    private var _binding: ViewCustomInformationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        args: Bundle?
    ): View {
        _binding = ViewCustomInformationBinding.inflate(inflater, parent, false)

        return requireNotNull(_binding).root
    }

    override fun notifyChannelChanged(channel: GroupChannel) {
        val keys = listOf("side_a", "side_b", "side_a_info", "side_b_info", "correlation_info")
        channel.getMetaData( keys, MetaDataHandler {
                metaData, error ->
            if (error != null) {
                Log.e("DebateChatHeader", "Error fetching metadata: ${error.message}")
                return@MetaDataHandler
            }
            binding.sideATitle.text = metaData?.get("side_a")
            binding.sideBTitle.text = metaData?.get("side_b")
            binding.sideAContent.text = metaData?.get("side_a_info")
            binding.sideBContent.text = metaData?.get("side_b_info")
            binding.supplementalInfoContent.text = metaData?.get("correlation_info")
        })

    }

   }


package com.example.thoughtbattle.ui.debate.information

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.thoughtbattle.MainActivity
import com.example.thoughtbattle.R
import com.example.thoughtbattle.databinding.ViewCustomHeaderBinding
import com.example.thoughtbattle.ui.debate.DebateChatFragment
import com.sendbird.android.channel.OpenChannel
import com.sendbird.android.handler.MetaDataHandler
import com.sendbird.uikit.activities.OpenChannelSettingsActivity
import com.sendbird.uikit.fragments.OpenChannelSettingsFragment
import com.sendbird.uikit.modules.components.OpenChannelHeaderComponent




class DebateChatHeader : OpenChannelHeaderComponent() {
    private var _binding: ViewCustomHeaderBinding? = null
    private val binding get() = _binding!!
    private var channelUrl: String? = null
    private var context: Context? = null // Store the context

    override fun onCreateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        args: Bundle?
    ): View {
        this.context = context
        _binding = ViewCustomHeaderBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun notifyChannelChanged(channel: OpenChannel) {
        binding.title.text = channel.name

        // Get sideA and sideB info from channel metadata
        val keys = listOf("side_a", "side_b")
        channel.getMetaData(keys, MetaDataHandler { metaData, error ->
            if (error != null) {
                Log.e("DebateChatHeader", "Error fetching metadata: ${error.message}")
                return@MetaDataHandler
            }
            binding.sideATitle.text = metaData?.get("side_a")
            binding.sideBTitle.text = metaData?.get("side_b")

            binding.infoButton.setOnClickListener {
                // Ensure context is not null
                context?.let {
                    val intent = OpenChannelSettingsActivity.newIntent(it, channel.url)
                    startActivity(it, intent, null)
                }
            }
        })
    }
}



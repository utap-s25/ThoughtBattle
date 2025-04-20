package com.example.thoughtbattle.ui.debate.information

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.thoughtbattle.databinding.ViewCustomInformationBinding
import com.example.thoughtbattle.ui.MainViewModel
import com.sendbird.android.channel.OpenChannel
import com.sendbird.android.handler.MetaDataHandler
import com.sendbird.uikit.fragments.OpenChannelSettingsFragment
import com.sendbird.uikit.modules.components.OpenChannelSettingsInfoComponent
import com.sendbird.uikit.vm.OpenChannelSettingsViewModel



class DebateSettingsFragment : OpenChannelSettingsFragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var debateInfoComponent: DebateInfoComponent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        return super.onCreateView(inflater, container, savedInstanceState)
    }


}

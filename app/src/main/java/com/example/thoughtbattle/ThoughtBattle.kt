package com.example.thoughtbattle

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import com.example.thoughtbattle.data.repository.SendBirdRepository
import com.example.thoughtbattle.ui.auth.AuthUser
import com.example.thoughtbattle.ui.main.DebateListFragment
import com.google.firebase.FirebaseApp
import com.sendbird.uikit.fragments.ChannelListFragment
import com.sendbird.uikit.fragments.OpenChannelListFragment
import com.sendbird.uikit.interfaces.providers.ChannelListFragmentProvider
import com.sendbird.uikit.interfaces.providers.OpenChannelListFragmentProvider
import com.sendbird.uikit.interfaces.providers.OpenChannelListModuleProvider
import com.sendbird.uikit.modules.OpenChannelListModule
import com.sendbird.uikit.providers.FragmentProviders
import com.sendbird.uikit.providers.ModuleProviders

//apparently having this class will be really important since i think we will need a global context?? yeah lol
class ThoughtBattle : Application() {
    private lateinit var authUser: AuthUser

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        SendBirdRepository.initialize(this)
        FragmentProviders.openChannelList = OpenChannelListFragmentProvider { args ->
            OpenChannelListFragment.Builder().withArguments(args).setCustomFragment(DebateListFragment())
                .setUseHeader(false).setUseHeaderRightButton(false)
                .build()
        }


    }


    }

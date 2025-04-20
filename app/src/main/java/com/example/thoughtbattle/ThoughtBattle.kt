package com.example.thoughtbattle

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import com.example.thoughtbattle.data.repository.SendBirdRepository
import com.example.thoughtbattle.ui.auth.AuthUser
import com.example.thoughtbattle.ui.debate.DebateChatFragment
import com.example.thoughtbattle.ui.debate.information.DebateChatHeader
import com.example.thoughtbattle.ui.debate.information.DebateInfoComponent
import com.example.thoughtbattle.ui.debate.information.DebateSettingsFragment
import com.example.thoughtbattle.ui.main.DebateListFragment
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.sendbird.uikit.fragments.ChannelListFragment
import com.sendbird.uikit.fragments.OpenChannelFragment
import com.sendbird.uikit.fragments.OpenChannelListFragment
import com.sendbird.uikit.interfaces.providers.ChannelListFragmentProvider
import com.sendbird.uikit.interfaces.providers.ChannelModuleProvider
import com.sendbird.uikit.interfaces.providers.OpenChannelFragmentProvider
import com.sendbird.uikit.interfaces.providers.OpenChannelListFragmentProvider
import com.sendbird.uikit.interfaces.providers.OpenChannelListModuleProvider
import com.sendbird.uikit.interfaces.providers.OpenChannelModuleProvider
import com.sendbird.uikit.interfaces.providers.OpenChannelSettingsModuleProvider
import com.sendbird.uikit.modules.OpenChannelListModule
import com.sendbird.uikit.modules.OpenChannelModule
import com.sendbird.uikit.modules.OpenChannelSettingsModule
import com.sendbird.uikit.providers.FragmentProviders
import com.sendbird.uikit.providers.ModuleProviders

//apparently having this class will be really important since i think we will need a global context?? yeah lol
class ThoughtBattle : Application() {


    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )
        SendBirdRepository.initialize(this)
        FragmentProviders.openChannelList = OpenChannelListFragmentProvider { args ->
            OpenChannelListFragment.Builder().withArguments(args)
                .setCustomFragment(DebateListFragment())
                .setUseHeader(false).setUseHeaderRightButton(false)
                .build()
        }

        ModuleProviders.openChannelSettings = OpenChannelSettingsModuleProvider { context, _ ->
            OpenChannelSettingsModule(context).apply {
                setOpenChannelSettingsInfoComponent(DebateInfoComponent(DebateSettingsFragment()))
            }



        }

        ModuleProviders.openChannel = OpenChannelModuleProvider { context, _ -> .
            val module = OpenChannelModule(context)

            module.setHeaderComponent(DebateChatHeader())
            module
        }
        FragmentProviders.openChannel = OpenChannelFragmentProvider { channelUrl,args ->
            OpenChannelFragment.Builder(channelUrl).
                setCustomFragment(DebateChatFragment())
                .setUseHeader(false).setUseHeaderRightButton(true)
                .build()
        }


    }



    }

package com.example.thoughtbattle

import android.app.Application
import android.provider.UserDictionary.Words.APP_ID
import android.util.Log
import com.example.thoughtbattle.data.repository.SendBirdRepository
import com.example.thoughtbattle.ui.debate.DebateChatFragment
import com.example.thoughtbattle.ui.debate.information.DebateChatHeader
import com.example.thoughtbattle.ui.debate.information.DebateInfoComponent
import com.example.thoughtbattle.ui.debate.information.DebateSettingsFragment
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.sendbird.android.SendbirdChat
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.params.InitParams
import com.sendbird.uikit.fragments.ChannelFragment
import com.sendbird.uikit.interfaces.providers.ChannelFragmentProvider
import com.sendbird.uikit.interfaces.providers.ChannelModuleProvider
import com.sendbird.uikit.interfaces.providers.ChannelSettingsModuleProvider
import com.sendbird.uikit.modules.ChannelModule
import com.sendbird.uikit.modules.ChannelSettingsModule
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
        SendbirdChat.init(
            InitParams(BuildConfig.SENDBIRD_APP_ID, this, useCaching = true) , object : InitResultHandler
         {
                override fun onMigrationStarted() {
                    Log.i("Application", "Called when there's an update in Sendbird server.")
                }

                override fun onInitFailed(e: SendbirdException) {
                    Log.i(
                        "Application",
                        "Called when initialize failed. SDK will still operate properly as if useLocalCaching is set to false."
                    )
                }

                override fun onInitSucceed() {
                    Log.i("Application", "Called when initialization is completed.")
                }
            })








        ModuleProviders.channelSettings = ChannelSettingsModuleProvider { context, _ ->
            ChannelSettingsModule(context).apply {
                setChannelSettingsInfoComponent(DebateInfoComponent(DebateSettingsFragment()))

            }



        }


        ModuleProviders.channel = ChannelModuleProvider { context, _ ->
            val module = ChannelModule(context)

            module.setHeaderComponent(DebateChatHeader())
            module
        }
        FragmentProviders.channel = ChannelFragmentProvider { channelUrl,args ->
            ChannelFragment.Builder(channelUrl).
                setCustomFragment(DebateChatFragment())
                .setUseHeader(false).setUseHeaderRightButton(true)
                .build()
        }




    }



    }

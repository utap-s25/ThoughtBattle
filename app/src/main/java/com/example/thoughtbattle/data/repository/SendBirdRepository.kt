package com.example.thoughtbattle.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.thoughtbattle.BuildConfig
import com.example.thoughtbattle.ui.main.DebateListFragment
import com.sendbird.android.channel.OpenChannel
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.params.OpenChannelCreateParams
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter
import com.sendbird.uikit.fragments.OpenChannelListFragment
import com.sendbird.uikit.interfaces.UserInfo
import com.sendbird.uikit.interfaces.providers.OpenChannelListFragmentProvider
import com.sendbird.uikit.providers.FragmentProviders

object SendBirdRepository {
    private const val APP_ID = BuildConfig.SENDBIRD_APP_ID
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
        sharedPreferences = context.getSharedPreferences("sendbird", Context.MODE_PRIVATE)

        SendbirdUIKit.init(object : SendbirdUIKitAdapter {
            override fun getAppId(): String {
                return APP_ID
            }

            override fun getAccessToken(): String {
                return ""
            }

            override fun getUserInfo(): UserInfo {
                return object : UserInfo {
                    override fun getUserId(): String {
                        val userId = sharedPreferences.getString("user_id", "") ?: ""
                        Log.d("SendBirdRepository", "getUserId: $userId")
                        return userId
                    }

                    override fun getNickname(): String {
                        val userName = sharedPreferences.getString("user_nickname", "") ?: ""
                        return userName
                    }

                    override fun getProfileUrl(): String {
                        val userProfile = sharedPreferences.getString("user_profile_pic", "") ?: ""
                        return userProfile
                    }
                }
            }

            override fun getInitResultHandler(): InitResultHandler {
                return object : InitResultHandler {
                    override fun onMigrationStarted() {
                        Log.d("SendBirdRepository", "onMigrationStarted")
                    }

                    override fun onInitFailed(e: SendbirdException) {
                        Log.e("SendBirdRepository", "onInitFailed", e)
                    }

                    override fun onInitSucceed() {
                        Log.d("SendBirdRepository", "onInitSucceed")

                    }
                }
            }
        }, context)
        FragmentProviders.openChannelList = OpenChannelListFragmentProvider { args ->
            OpenChannelListFragment.Builder().withArguments(args).setCustomFragment(DebateListFragment())
                .setUseHeader(false).setUseHeaderRightButton(false)
                .build()
        }

    }

    fun connect(userId: String) {
        SendbirdUIKit.connect{userId, e  ->
            if (e != null) {
                Log.e("SendBirdRepository", "Connect failed", e)
                return@connect
            }
            Log.d("SendBirdRepository", "Connect succeeded: ${userId}")
        }
    }

    fun disconnect() {
        SendbirdUIKit.disconnect(null)
    }

    fun createOpenChannel(name: String, callback: (String) -> Unit, userId: String) {
        val params = OpenChannelCreateParams().apply {
            this.name = name
            this.operatorUserIds = listOf(userId)
        }

        OpenChannel.createChannel(params) { channel, e ->
            if (e != null) {
                Log.e("SendBirdRepository", "Channel creation failed", e)
                return@createChannel
            }
            callback(channel?.url ?: "")
        }
    }
}
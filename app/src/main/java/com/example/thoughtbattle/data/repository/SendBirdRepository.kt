package com.example.thoughtbattle.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.thoughtbattle.BuildConfig
import com.example.thoughtbattle.data.model.Debate
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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException


object SendBirdRepository {
    private  val APP_ID = BuildConfig.SENDBIRD_APP_ID
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
    }suspend fun createOpenChannel(name: String, userId: String): String {
        return suspendCancellableCoroutine { continuation ->
            val params = OpenChannelCreateParams().apply {
                this.name = name
                this.operatorUserIds = listOf(userId)
            }

            OpenChannel.createChannel(params) { channel, e ->
                if (e != null) {
                    Log.e("SendBirdRepository", "Channel creation failed", e)
                    continuation.resumeWithException(e)
                    return@createChannel
                }
                continuation.resume(channel?.url ?: "") {
                    // Handle cancellation
                }
            }
        }
    }
    fun updateChannelMetaData(channelUrl: String, debate: Debate, callback: (Boolean, String?) -> Unit) {
        OpenChannel.getChannel(channelUrl) { channel, e ->
            if (e != null) {
                callback(false, "Channel retrieval failed: ${e.message}")
                return@getChannel
            }

            val metaData = mapOf(
                "debate_title" to debate.title,
                "side_a" to debate.sideA,
                "side_b" to debate.sideB,
                "side_a_info" to debate.sideAInfo,
                "side_b_info" to debate.sideBInfo,
                "correlation_info" to debate.correlationInfo,
                "creator_id" to debate.creatorId,
                "created_at" to debate.createdAt.toString()
            )

            channel?.createMetaData(metaData as Map<String, String>) { _, error ->
                if (error != null) {
                    callback(false, "Metadata creation failed: ${error.message}")
                } else {
                    callback(true, "Metadata created successfully")
                }
            }
        }
    }

}
package com.example.thoughtbattle.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.thoughtbattle.BuildConfig
import com.example.thoughtbattle.data.model.Debate
import com.example.thoughtbattle.ui.main.DebateListFragment
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.channel.query.MembershipFilter
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.params.ApplicationUserListQueryParams
import com.sendbird.android.params.GroupChannelCreateParams
import com.sendbird.android.params.PublicGroupChannelListQueryParams
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter
import com.sendbird.uikit.fragments.ChannelListFragment
import com.sendbird.uikit.interfaces.UserInfo
import com.sendbird.uikit.interfaces.providers.ChannelListFragmentProvider
import com.sendbird.uikit.providers.FragmentProviders
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException


object SendBirdRepository {
    private val APP_ID = BuildConfig.SENDBIRD_APP_ID
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var context: Context
    private lateinit var userList: MutableLiveData<MutableList<String>>


    @SuppressLint("StaticFieldLeak")
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


    }

    fun connect(userId: String) {
        SendbirdUIKit.connect { userId, e ->
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


    suspend fun createDebateChat(name: String, userId: String, debateTopic: String): String {
        return suspendCancellableCoroutine { continuation ->


            val params = GroupChannelCreateParams().apply {
                this.name = name
                this.operatorUserIds = listOf(userId)
                this.isPublic = true
                this.userIds = listOf(userId, "discussion_moderator")
                this.isDiscoverable = true
                this.customType = debateTopic


            }

            GroupChannel.createChannel(params) { channel, e ->
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
        GroupChannel.getChannel(channelUrl) { channel, e ->
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


suspend fun addBotToChannel(userId: String, channelUrls: List<String>) {
    val requestBody = SendbirdApi.SendbirdRequestBody(channelUrls)
    val apiService = SendbirdApi.create()
    try {
        val response = apiService.addBotToChannel(userId, requestBody)
        Log.d("SendBirdRepository", "Bot added to channels: $response")
    } catch (e: Exception) {
        Log.e("SendBirdRepository", "Error adding bot to channels", e)

    }

}

}
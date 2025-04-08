package com.example.thoughtbattle.data.repository

import android.content.Context
import android.util.Log
import com.example.thoughtbattle.BuildConfig
import com.sendbird.android.SendbirdChat
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.android.params.InitParams
import com.sendbird.android.params.OpenChannelCreateParams
import com.sendbird.android.params.OpenChannelListQueryParams
import com.sendbird.android.channel.OpenChannel

object SendBirdManager {
    private const val APP_ID = BuildConfig.SENDBIRD_APP_ID

    fun initialize(context: Context) {
        SendbirdChat.init(
            InitParams(APP_ID, context, true),
            object : InitResultHandler {
                override fun onInitFailed(e: SendbirdException) {

                }

                override fun onInitSucceed() {

                }

                override fun onMigrationStarted() {

                }
            }
        )
    }

    fun connect(userId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        SendbirdChat.connect(userId) { user, e ->
            if (e != null) {
                onError(Exception("SendBird connection failed: ${e.message}"))
                return@connect
            }
            onSuccess()
        }
    }

    fun disconnect() {
        SendbirdChat.disconnect(null)
    }

    fun createOpenChannel(name: String, callback: (String) -> Unit) {
        val params = OpenChannelCreateParams().apply {
            this.name = name
            this.operatorUserIds = listOf(SendbirdChat.currentUser?.userId ?: "")
        }

        OpenChannel.createChannel(params) { channel, e ->
            if (e != null) {
                Log.e("SendBirdManager", "Channel creation failed", e)
                return@createChannel
            }
            callback(channel?.url ?: "")
        }
    }

    fun getOpenChannels(callback: (List<OpenChannel>) -> Unit) {
        val params = OpenChannelListQueryParams().apply {
            limit = 100 //we can change that later lol
        }

        val listQuery = OpenChannel.createOpenChannelListQuery(params)
        listQuery.next { channels, e ->
            if (e != null) {
                Log.e("SendBirdManager", "Channel list query failed", e)
                callback(emptyList())
                return@next
            }
            callback(channels ?: emptyList())
        }
    }
}
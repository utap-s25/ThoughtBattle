package com.example.thoughtbattle.ui.debate

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thoughtbattle.databinding.ActivityDebateChatBinding
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.BaseChannel
import com.sendbird.android.channel.OpenChannel
import com.sendbird.android.handler.OpenChannelHandler
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.params.PreviousMessageListQueryParams


class DebateChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDebateChatBinding
    private lateinit var channelUrl: String
    private lateinit var channel: OpenChannel
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebateChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        channelUrl = intent.getStringExtra("channel_url") ?: run {
            finish()
            return
        }

        setupRecyclerView()
        loadChannel()

        binding.sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DebateChatActivity)
            adapter = messageAdapter
        }
    }

    private fun loadChannel() {
        OpenChannel.getChannel(channelUrl) { channel, e ->
            if (e != null || channel == null) {
                Toast.makeText(this, "Failed to load channel", Toast.LENGTH_SHORT).show()
                finish()
                return@getChannel
            }

            this.channel = channel
            binding.channelName.title = channel.name

            // Enter channel
            channel.enter { e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to enter channel", Toast.LENGTH_SHORT).show()
                    finish()
                    return@enter
                }

                loadMessages()
                setupMessageHandler()
            }
        }
    }

    private fun loadMessages() {

        val params = PreviousMessageListQueryParams().apply {
            // TODO: we will change this later
            limit = 100
            reverse = false
        }


        val listQuery = channel.createPreviousMessageListQuery(params)

        listQuery.load { messages, e ->
            if (e != null) {
                Log.e("DebateChat", "Failed to load messages", e)
                return@load
            }

            // Messages are returned in chronological order (oldest first)

            messageAdapter.submitList(messages?.reversed())
            binding.messagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }



    private fun setupMessageHandler() {
        val handler = object : OpenChannelHandler() {
            override fun onMessageReceived(channel: BaseChannel, message: BaseMessage) {
                if (channel.url == this@DebateChatActivity.channel.url) {
                    messageAdapter.addMessage(message)
                    binding.messagesRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        }

        SendbirdChat.addChannelHandler("DEBATE_CHAT_HANDLER", handler)
    }

    private fun sendMessage() {
        val messageText = binding.messageEditText.text.toString()
        if (messageText.isEmpty()) return

        channel.sendUserMessage(messageText) { message, e ->
            if (e != null) {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                return@sendUserMessage
            }

            binding.messageEditText.text.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::channel.isInitialized) {
            channel.exit(null)
        }
        SendbirdChat.removeChannelHandler("DEBATE_CHAT_HANDLER")
    }
}
package com.example.thoughtbattle.ui.debate

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thoughtbattle.databinding.ItemReceiverMessageBinding
import com.example.thoughtbattle.databinding.ItemSenderMessageBinding
import com.sendbird.android.SendbirdChat
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.message.UserMessage
import java.util.Date

class MessageAdapter : ListAdapter<BaseMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_MY_MESSAGE = 1
        private const val VIEW_TYPE_OTHER_MESSAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.sender?.userId == SendbirdChat.currentUser?.userId) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MY_MESSAGE -> {
                val binding = ItemReceiverMessageBinding.inflate(inflater, parent, false)
                MyMessageViewHolder(binding)
            }
            VIEW_TYPE_OTHER_MESSAGE -> {
                val binding = ItemSenderMessageBinding.inflate(inflater, parent, false)
                OtherMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is MyMessageViewHolder -> holder.bind(message)
            is OtherMessageViewHolder -> holder.bind(message)
        }
    }

    inner class MyMessageViewHolder(private val binding: ItemReceiverMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: BaseMessage) {
            if (message is UserMessage) {
                binding.messageText.text = message.message
                binding.messageTime.text = getTime(message.createdAt)
            }
        }
    }

    inner class OtherMessageViewHolder(private val binding: ItemSenderMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: BaseMessage) {
            if (message is UserMessage) {
                binding.senderName.text = message.sender?.nickname ?: "Unknown"
                binding.messageText.text = message.message
                binding.messageTime.text = getTime(message.createdAt)
            }
        }
    }

    fun addMessage(message: BaseMessage) {
        val newList = currentList.toMutableList().apply { add(message) }
        submitList(newList)
    }

    private class DiffCallback : DiffUtil.ItemCallback<BaseMessage>() {
        override fun areItemsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
            return oldItem.messageId == newItem.messageId &&
                    (oldItem as? UserMessage)?.message == (newItem as? UserMessage)?.message &&
                    oldItem.createdAt == newItem.createdAt
        }
    }
    private fun getTime(timestamp: Long): String {
        val date = Date(timestamp)
        return DateFormat.format("hh:mm a", date).toString()
    }
}
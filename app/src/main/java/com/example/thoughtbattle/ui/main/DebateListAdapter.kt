package com.example.thoughtbattle.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thoughtbattle.databinding.ListItemChannelBinding
import com.sendbird.android.channel.GroupChannel

class DebateListAdapter : RecyclerView.Adapter<DebateListAdapter.ChannelViewHolder>() {
    private val channels = mutableListOf<GroupChannel>()
    private var itemClickListener: ((GroupChannel) -> Unit)? = null

    fun setOnItemClickListener(listener: (GroupChannel) -> Unit) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding = ListItemChannelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChannelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(channels[position])
    }

    override fun getItemCount() = channels.size

    fun setChannels(newChannels: List<GroupChannel>) {
        channels.clear()
        channels.addAll(newChannels)
        notifyDataSetChanged()
    }

    fun addChannels(newChannels: List<GroupChannel>) {
        val startPosition = channels.size
        channels.addAll(newChannels)
        notifyItemRangeInserted(startPosition, newChannels.size)
    }

    fun clearChannels() {
        channels.clear()
        notifyDataSetChanged()
    }

    inner class ChannelViewHolder(private val binding: ListItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(channel: GroupChannel) {
            binding.apply {
                textChannelName.text = channel.name
                textChannelMemberCount.text = channel.memberCount.toString()
                textChannelRecent.text = channel.lastMessage?.message ?: "No messages"

                root.setOnClickListener {
                    itemClickListener?.invoke(channel)
                }
            }
        }
    }
}
package com.example.thoughtbattle.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thoughtbattle.databinding.ItemChannelBinding
import com.sendbird.android.channel.OpenChannel


class ChannelAdapter(private val onClick: (OpenChannel) -> Unit) :
    RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    private var channels: List<OpenChannel> = emptyList()

    inner class ChannelViewHolder(val binding: ItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding = ItemChannelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ChannelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]
        holder.binding.channelName.text = channel.name
        holder.binding.participantCount.text = "${channel.participantCount} participants"

        holder.itemView.setOnClickListener {
            onClick(channel)
        }
    }

    override fun getItemCount() = channels.size

    fun updateChannels(newChannels: List<OpenChannel>) {
        channels = newChannels
        notifyDataSetChanged()
    }
}
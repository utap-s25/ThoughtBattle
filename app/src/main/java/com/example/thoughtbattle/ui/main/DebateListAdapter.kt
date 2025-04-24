package com.example.thoughtbattle.ui.main

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.thoughtbattle.R
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
            val topicIcon = when (channel.customType) {
                "History" -> R.drawable.ic_history
                "Politics" -> R.drawable.ic_politics
                "Science" -> R.drawable.ic_science
                "Technology" -> R.drawable.ic_technology
                "Sports" -> R.drawable.ic_sports
                "Finance" -> R.drawable.ic_finance
                "Environment" -> R.drawable.ic_enviroment
                "Pop Culture" -> R.drawable.ic_popculture
                "Gaming" -> R.drawable.ic_gaming
                "Arts" -> R.drawable.ic_art
                "Media" -> R.drawable.ic_media
                "Health" -> R.drawable.ic_health
                "Religion" -> R.drawable.ic_religion
                "Conspiracy" -> R.drawable.ic_conspiracy
                "Entertainment" -> R.drawable.ic_entertainment
                "Social" -> R.drawable.ic_social
                "Education" -> R.drawable.ic_education
                "Film and Books" -> R.drawable.ic_media
                else -> R.drawable.ic_other
            }
            binding.apply {
                imageChannelIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, topicIcon))
                textChannelName.text = channel.name
                textChannelMemberCount.text = String.format(channel.memberCount.toString())
                textChannelRecent.text = channel.lastMessage?.message ?: "No messages"

                root.setOnClickListener {
                    itemClickListener?.invoke(channel)
                }
            }
        }
    }
}
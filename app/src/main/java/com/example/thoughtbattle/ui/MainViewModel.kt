package com.example.thoughtbattle.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thoughtbattle.data.repository.SendBirdManager
import com.sendbird.android.channel.OpenChannel


class MainViewModel : ViewModel() {
    private val _channels = MutableLiveData<List<OpenChannel>>()
    val channels: LiveData<List<OpenChannel>> = _channels

    fun loadChannels() {

        SendBirdManager.getOpenChannels { channels ->
            _channels.postValue(channels)
        }
    }
}
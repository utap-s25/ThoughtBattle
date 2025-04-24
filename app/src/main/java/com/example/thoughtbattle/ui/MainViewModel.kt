package com.example.thoughtbattle.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.thoughtbattle.data.model.Debate
import com.example.thoughtbattle.data.model.User
import com.example.thoughtbattle.data.model.invalidUser
import com.example.thoughtbattle.data.repository.FirebaseRepository
import com.example.thoughtbattle.data.repository.GeminiRepository

import com.example.thoughtbattle.data.repository.SendBirdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private var currentAuthUser = invalidUser
    private var loggedInUser = MutableLiveData<User>()
    private var debateInfo = MutableLiveData<Debate>()
    private var geminiRepistory = GeminiRepository()

    //https://developer.android.com/reference/androidx/lifecycle/Transformations
    //https://medium.com/@kalyanraghu/an-example-of-livedata-switchmap-transformation-function-ceddb1a44c58
//whenever the current debateUrl is changed it causes the debate to be fetched from the database
    //isnt this neat and pretty handy????
    private var currentDebateUrl = MutableLiveData<String>()
    val debate: LiveData<Debate?> = currentDebateUrl.switchMap { channelUrl ->
        val debateLiveData = MutableLiveData<Debate?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedDebate = FirebaseRepository.getDebateByChannelUrl(channelUrl)
                debateLiveData.postValue(fetchedDebate)
            } catch (e: Exception) {
                debateLiveData.postValue(null)
            }
        }
        debateLiveData
    }

    fun addnewUsersToChannel(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                addnewUsersToChannel(userId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to add new users to channel")
            }
        }
    }


    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
        loggedInUser.postValue(user)


    }


    fun fetchDebateInfo(channelUrl: String): Debate {
        Log.d("MainViewModel", "Fetching debate info for channelUrl: $channelUrl")
        //i didnt know how to properly name this :(
        var fetchedDebate = Debate()
        viewModelScope.launch(Dispatchers.IO) {
            fetchedDebate = FirebaseRepository.getDebateByChannelUrl(channelUrl)!!
            Log.d("MainViewModel", "Fetched debate: $fetchedDebate")
        }
        return fetchedDebate

    }


    fun createnewDebate(
        title: String,
        sideA: String,
        sideB: String,
        debateTopic: String,
        onSuccess: (Debate) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Generate debate content
                val sideAinfo = geminiRepistory.generateDebateSideInfo(title, sideA)
                val sideBinfo = geminiRepistory.generateDebateSideInfo(title, sideB)
                val correlationInfo = geminiRepistory.generateCorrelationInfo(title, sideA, sideB)

                // Create channel
                val channelUrl =
                    SendBirdRepository.createDebateChat(title, currentAuthUser.id, debateTopic)

                // Create debate object
                val debate = Debate(
                    title = title,
                    sideA = sideA,
                    sideB = sideB,
                    channelUrl = channelUrl,
                    sideAInfo = sideAinfo,
                    sideBInfo = sideBinfo,
                    correlationInfo = correlationInfo,
                    creatorId = currentAuthUser.id,
                    createdAt = System.currentTimeMillis()
                )


                FirebaseRepository.createDebate(debate)

                Log.d("MainViewModel", "Adding channel metadeta")
                SendBirdRepository.updateChannelMetaData(channelUrl, debate) { success, message ->
                    if (success) {
                        Log.d("MainViewModel", "Channel metadata updated successfully")
                        onSuccess(debate)
                    } else {
                        Log.e("MainViewModel", "Channel metadata update failed: $message")
                        onError(message ?: "Unknown error")
                    }
                }

                //add discussion moderator bot to channel :)
                Log.d("MainViewModel", "Adding bot to channel: $channelUrl")
                SendBirdRepository.addBotToChannel("discussion_moderator", listOf(channelUrl))


            } catch (e: Exception) {
                onError(e.message ?: "Debate creation failed")
            }
        }

    }



    fun setCurrentDebateUrl(channelUrl: String) {
        currentDebateUrl.value = channelUrl

    }
    fun observeDebate(): LiveData<Debate?> {
        return debate

    }

    fun observerUserAfterLogin():LiveData<User > {
        return loggedInUser
    }

}
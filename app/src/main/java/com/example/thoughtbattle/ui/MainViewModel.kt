package com.example.thoughtbattle.ui

import android.net.Uri
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
import com.sendbird.android.channel.GroupChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel : ViewModel() {
    private val _currentAuthUser = MutableLiveData<User>()
    val currentAuthUser: LiveData<User> = _currentAuthUser


    private var debateInfo = MutableLiveData<Debate>()
    private var geminiRepistory = GeminiRepository()
    private val _errorState = MutableLiveData<String>()
    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState = _loadingState
    private val userId = MutableLiveData<String>()
    private val _userProfile = userId.switchMap { id:String->
        val userProfileData = MutableLiveData<User>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    val user = FirebaseRepository.getUser(id)
                    userProfileData.postValue(user)
                }

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error getting user profile", e)
                userProfileData.postValue(invalidUser)
            }
        }
        userProfileData

    }


    val errorState: LiveData<String> = _errorState
    private var loggedInUser = currentAuthUser.switchMap { user: User ->
     Log.d("MainViewModel", "user profile during switchMap of logged in User  $user" )
        val userLiveData = MutableLiveData<User>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userExists = FirebaseRepository.checkIfUserInFirebase(user.id)
                if (userExists) {
                    Log.d("MainViewModel", "User exists in Firebase")
                    val existingUser = FirebaseRepository.getFirestoreUser(user.id)
                    userLiveData.postValue(existingUser)
                } else {
                    Log.d("MainViewModel", "User does not exist in Firebase")
                    FirebaseRepository.createUser(user)
                    userLiveData.postValue(user)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error checking or creating user", e)
                userLiveData.postValue(invalidUser)
            }
        }
        userLiveData
    }
    //could be the currently logged in user or the loggd in user visiting another profile
    private var userProfile = userId.switchMap {
        Log.d("MainViewModel", "Fetching user profile for user ID: $it")
        val userProfileData = MutableLiveData<User>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = FirebaseRepository.getFirestoreUser(it)
                Log.d("MainViewModel", "Fetched user profile: $user")
                userProfileData.postValue(user)

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error getting user profile", e)
                userProfileData.postValue(invalidUser)

            }
        }
        userProfileData
    }




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
            _currentAuthUser.postValue(user)
            _loadingState.postValue(false)


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
                    val correlationInfo =
                        geminiRepistory.generateCorrelationInfo(title, sideA, sideB)

                    // Create channel
                    val channelUrl =
                        SendBirdRepository.createDebateChat(title, currentAuthUser.value!!.id, debateTopic)

                    // Create debate object
                    val debate = Debate(
                        title = title,
                        sideA = sideA,
                        sideB = sideB,
                        channelUrl = channelUrl,
                        sideAInfo = sideAinfo,
                        sideBInfo = sideBinfo,
                        correlationInfo = correlationInfo,
                        creatorId = currentAuthUser.value!!.id,
                        createdAt = System.currentTimeMillis()
                    )


                    FirebaseRepository.createDebate(debate)

                    Log.d("MainViewModel", "Adding channel metadeta")
                    SendBirdRepository.updateChannelMetaData(
                        channelUrl,
                        debate
                    ) { success, message ->
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


        fun uploadPhoto(photoUri: Uri, onSuccess: (Uri) -> Unit, onError: (String) -> Unit) {
            viewModelScope.launch(Dispatchers.IO) {

                val downloadUrl = FirebaseRepository.uploadProfileImage(photoUri)
                onSuccess(downloadUrl)
                onError("Upload failed")

            }
        }


        fun loadCurrentUserProfile(): User {
            var currentUserProfile = User()
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    currentUserProfile = FirebaseRepository.getUser(currentAuthUser.value!!.id)
                   // _userProfile.postValue(currentUserProfile)


                } catch (e: Exception) {
                    // Handle error
                }
            }
            return currentUserProfile
        }

        fun loadUserProfile(uid: String) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val user = FirebaseRepository.getUser(uid)
                 //   _userProfile.postValue(user)
                } catch (e: Exception) {

                }
            }
        }

        fun updateProfile(displayName: String, imageUri: Uri?) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    loadingState.postValue(true)
                    // Upload image and update profile
                    val photoUrl = imageUri?.let { FirebaseRepository.uploadProfileImage(it) }

                    FirebaseRepository.updateUserProfile(displayName, photoUrl)
                    val updates = hashMapOf<String, Any>(
                        "username" to displayName,

                    )
                    FirebaseRepository.updateFirestoreUser(updates)


                    loadCurrentUserProfile()
                    loadingState.postValue(false)
                } catch (e: Exception) {
                    // Handle error
                }
            }

        }

        fun observeUserProfile(): LiveData<User> {
            return _userProfile
        }

        fun updateUserDebateHistory(channel: GroupChannel) {
            viewModelScope.launch(Dispatchers.IO) {
                try {

                    currentAuthUser.value?.let {
                        val debate = FirebaseRepository.getDebateByChannelUrl(channel.url)
                        FirebaseRepository.addDebateToUserHistory(it.id, debate.toString())
                        Log.d("MainViewModel", "Updated user debate history: ${it.debateHistory}")
                        val history = FirebaseRepository.getUserDebateHistory(it.id)
                       val recommendaiton =  geminiRepistory.generateDebateRecs(history)
                        Log.d("MainViewModel", "Updated user debate history: $recommendaiton")
                        FirebaseRepository.updateUserDebateReccomendations(it.id, recommendaiton)


                    }



            }catch(e:Exception) {

                //handle error

            }



}
    }
    //create a method where we set the id before visiting profile of user
    fun setUserId(id: String) {
        userId.value = id
    }


    fun observeAuthUser(): LiveData<User> {
        return loggedInUser
    }
    fun logChannelJoinEvent(id: String, title:String, userId:String, topic:String) {
        FirebaseRepository.onChannelJoined(id, userId, title, topic)
    }
}
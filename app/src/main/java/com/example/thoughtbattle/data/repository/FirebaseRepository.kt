package com.example.thoughtbattle.data.repository

import android.net.Uri
import android.util.Log
import com.example.thoughtbattle.ThoughtBattle
import com.example.thoughtbattle.data.model.Debate
import com.example.thoughtbattle.data.model.User
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi


import kotlinx.coroutines.tasks.await
import java.util.Arrays
import java.util.UUID



object FirebaseRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val debateCollection =   db.collection("debates")
    private val userCollection = db.collection("users")
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    val firebaseAnalytics = FirebaseAnalytics.getInstance(ThoughtBattle())


    fun createDebate(debate: Debate){
        debateCollection.add(debate)
    }
    suspend fun createUser(user: User) {
        Log.d("FirebaseRepository", "Creating user with ID ${user.id} in Firestore")
        try {
            val userRef = userCollection.document(user.id)
            userRef.set(user).await()
            Log.d("FirebaseRepository", "User created successfully")
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error creating user", e)
            throw e
        }
    }
    fun updateUser(user: User){
        userCollection.document(user.id).set(user)

    }
    suspend fun addDebateToUserHistory(userId: String, debate: String){
        Log.d("FirebaseRepository", "Adding debate to user history with ID $userId")
        userCollection.document(userId).update("debateHistory", FieldValue.arrayUnion(Arrays.asList(debate))).await()
    }
    fun updateDebate(debate: Debate){
        debateCollection.document(debate.id).set(debate)

    }
    suspend fun getDebate(id: String): Debate {
        val documentSnapshot = debateCollection.document(id).get().await()
        if (documentSnapshot.exists()) {
            val debate = documentSnapshot.toObject(Debate::class.java)!!
            Log.d("FirebaseRepository", "Fetched user: $debate")
            return debate
        } else {
            Log.d("FirebaseRepository", "debate document does not exist for ID: $id")
            throw Exception("debate document does not exist")
        }
    }
   @OptIn(ExperimentalCoroutinesApi::class)
   suspend fun getUser(id: String): User {
       val documentSnapshot = userCollection.document(id).get().await()
       if (documentSnapshot.exists()) {
           val user = documentSnapshot.toObject(User::class.java)!!
           Log.d("FirebaseRepository", "Fetched user: $user")
           return user
       } else {
           Log.d("FirebaseRepository", "User document does not exist for ID: $id")
           throw Exception("User document does not exist")
       }
    }
  fun deleteDebate(id: String){
      debateCollection.document(id).delete()

  }
    suspend fun getDebateByChannelUrl(channelUrl: String): Debate? {
        Log.d("FirebaseRepository", "Fetching debate with channelUrl: $channelUrl")
        val query = db.collection("debates").whereEqualTo("channelUrl", channelUrl)
        val querySnapshot = query.get().await()
        return querySnapshot.documents.firstOrNull()?.toObject(Debate::class.java)
    }



    suspend fun uploadProfileImage(uri: Uri): Uri {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val uuid = UUID.randomUUID().toString()
        val storageRef = storage.reference.child("profile_images/${uuid}.jpg")
        val uploadTask = storageRef.putFile(uri).await()
        val updates = hashMapOf<String, Any>(
            "profileImageUrl" to storageRef.downloadUrl.await().toString(),
            "uuid" to uuid
        )
        updateFirestoreUser(updates)
        userCollection.document(userId).update(updates).await()
        return storageRef.downloadUrl.await()
    }

    suspend fun updateUserProfile(displayName: String, photoUrl: Uri?) {
        val user = auth.currentUser ?: throw Exception("User not logged in")
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .apply { photoUrl?.let { photoUri = it } }
            .build()

        user.updateProfile(profileUpdates).await()
    }

    suspend fun updateFirestoreUser(updates: Map<String, Any>) {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        db.collection("users").document(userId).update(updates).await()
    }

    suspend fun getFirestoreUser(userId:String): User {
        var user = User()
        userCollection.document(userId).get().addOnSuccessListener {
            user = it.toObject(User::class.java)!!
        }
        return user


    }
    fun onChannelJoined(channelUrl: String, userId: String, debateTitle: String, categoryName: String)
        {
            logAnalyticsEvent(userId, channelUrl, categoryName, debateTitle)
        }


        private fun logAnalyticsEvent(userId:String, id: String,categoryName: String,debateTitle: String) {
            firebaseAnalytics.setUserId(userId)

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.ITEM_ID, id)
                param(FirebaseAnalytics.Param.ITEM_NAME, debateTitle)
                param(FirebaseAnalytics.Param.CONTENT_TYPE, categoryName)

            }
            Log.d("FirebaseRepository", "Analytics event logged")
        }


        suspend fun checkIfUserInFirebase(userId: String): Boolean {
                Log.d("FirebaseRepository", "Checking if user with ID $userId exists in Firebase")
                return try {
                    val document = userCollection.document(userId).get().await()

                    val exists = document.exists()
                    Log.d("FirebaseRepository", "Document exists: $exists")
                    exists
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "Error checking user existence", e)
                    false
                }
            }
    suspend fun getUserDebateHistory(userId: String): List<String> {
        val userDoc = userCollection.document(userId).get().await()
        val user = userDoc.toObject(User::class.java)
        if (user != null) {
            Log.d("FirebaseRepository", "Fetched user: ${user.debateHistory}")
        }
        return user?.debateHistory?: emptyList()

    }
    suspend fun updateUserDebateReccomendations(userId: String, reccomendation:String) {
        Log.d("FirebaseRepository", "Adding debate to user history with ID $reccomendation")
        userCollection.document(userId).update("debateRecommendation", reccomendation).await()
    }
        }



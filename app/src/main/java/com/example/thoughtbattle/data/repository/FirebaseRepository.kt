package com.example.thoughtbattle.data.repository

import android.util.Log
import com.example.thoughtbattle.data.model.Debate
import com.example.thoughtbattle.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val debateCollection =   db.collection("debates")
    private val userCollection = db.collection("users")

    fun createDebate(debate: Debate){
        debateCollection.add(debate)
    }
    fun createUser(user: User){
        userCollection.add(user)
    }
    fun updateUser(user: User){
        userCollection.document(user.id).set(user)

    }
    fun updateDebate(debate: Debate){
        debateCollection.document(debate.id).set(debate)

    }
    fun getDebate(id: String): Debate {
        var debate = Debate()
        debateCollection.document(id).get().addOnSuccessListener {
            debate = it.toObject(Debate::class.java)!!
        }
        return debate
    }
    fun getUser(id: String): User {
        var user = User()
        userCollection.document(id).get().addOnSuccessListener {
            user = it.toObject(User::class.java)!!
        }
        return user
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
}
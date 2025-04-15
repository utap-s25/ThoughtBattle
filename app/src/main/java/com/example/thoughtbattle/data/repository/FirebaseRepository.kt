package com.example.thoughtbattle.data.repository

import com.example.thoughtbattle.data.model.Debate
import com.example.thoughtbattle.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

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
}
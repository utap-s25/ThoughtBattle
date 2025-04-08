package com.example.thoughtbattle.data.model


data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val debateHistory: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
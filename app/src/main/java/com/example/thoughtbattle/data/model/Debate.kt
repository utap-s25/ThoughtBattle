package com.example.thoughtbattle.data.model
// Debate.kt
data class Debate(
    val id: String = "",
    val title: String = "",
    val sideA: String = "",
    val sideB: String = "",
    val creatorId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val participantCount: Int = 0,
    val channelUrl: String = "",
    val isActive: Boolean = true
)
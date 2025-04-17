package com.example.thoughtbattle.data.model
import com.example.thoughtbattle.data.repository.FirebaseRepository
data class Debate(
    val id: String = "",
    val title: String = "",
    val sideA: String = "",
    val sideB: String = "",
   val sideAInfo: String? = "",
    val sideBInfo: String? = "",
    val sideAPhotoUrl: String? = "",
    val sideBPhotoUrl: String? = "",
    val creatorId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val participantCount: Int = 0,
    val channelUrl: String = "",
    val isActive: Boolean = true
){
    init {
        FirebaseRepository.createDebate(this)


    }
}

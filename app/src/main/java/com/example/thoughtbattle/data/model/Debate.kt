package com.example.thoughtbattle.data.model
import com.example.thoughtbattle.data.repository.FirebaseRepository
data class Debate(
    var id: String = "",
    var title: String = "",
    var sideA: String = "",
    var sideB: String = "",
    var sideAInfo: String? = "",
    var sideBInfo: String? = "",
    var correlationInfo: String? = "",
    val sideAPhotoUrl: String? = "",
    val sideBPhotoUrl: String? = "",
    var creatorId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val participantCount: Int? = 0,
    var channelUrl: String = "",
    val isActive: Boolean = true
){
    init {
        FirebaseRepository.createDebate(this)


    }
}

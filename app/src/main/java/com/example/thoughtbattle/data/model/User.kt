package com.example.thoughtbattle.data.model


import com.example.thoughtbattle.data.repository.FirebaseRepository
import com.google.gson.annotations.SerializedName
import com.sendbird.uikit.interfaces.UserInfo
import java.time.LocalDateTime

data class User(
    @SerializedName("userId")
    val id: String = "",

    @SerializedName("nickname")
    val username: String = "",

    val email: String = "",

    @SerializedName("profileUrl")
    val profileImageUrl: String = "",



    val debateHistory: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    init{
        FirebaseRepository.createUser(this)
    }

}



const val invalidUserUid = "-1"

fun User.isInvalid(): Boolean {
    return id == invalidUserUid
}
val invalidUser = User("null", "null",
    invalidUserUid)






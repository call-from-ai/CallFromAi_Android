package kr.co.call.network.dto.chatting

import com.google.gson.annotations.SerializedName

data class ChatHeaderDTO(
    @SerializedName("chatRoomId")
    val chatRoomId: Long,

    @SerializedName("characterId")
    val characterId: Long,

    @SerializedName("characterFirstName")
    val characterFirstName: String,

    @SerializedName("characterImageUrl")
    val characterImageUrl: String,

    @SerializedName("isMain")
    val isMain: Boolean,

    @SerializedName("dDay")
    val dDay: Int
)
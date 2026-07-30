package kr.co.call.network.dto.chatting

import com.google.gson.annotations.SerializedName

data class ChatSseMessageDto(
    @SerializedName("chatRoomId")
    val chatRoomId: Long,

    @SerializedName("chatMessageId")
    val chatMessageId: Long,

    @SerializedName("senderType")
    val senderType: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("messageType")
    val messageType: String,

    @SerializedName("createdAt")
    val createdAt: String
)

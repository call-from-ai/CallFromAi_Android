package kr.co.call.network.dto.chatting

import com.google.gson.annotations.SerializedName

data class ChatMessagesDTO(
    @SerializedName("content")
    val content: List<ChatMessageDTO>,

    @SerializedName("nextCursor")
    val nextCursor: Long?,

    @SerializedName("hasNext")
    val hasNext: Boolean
)

data class ChatMessageDTO(
    @SerializedName("chatMessageId")
    val chatMessageId: Long,

    @SerializedName("senderType")
    val senderType: String,

    @SerializedName("content")
    val content: String?,

    @SerializedName("messageType")
    val messageType: String,

    @SerializedName("photoUrl")
    val photoUrl: String?,

    @SerializedName("createdAt")
    val createdAt: String
)
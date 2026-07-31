package kr.co.call.network.dto.chatting

import com.google.gson.annotations.SerializedName

data class ChatRoomsDTO(
    @SerializedName("content")
    val content: List<ChatRoomDTO>
)

data class ChatRoomDTO(
    @SerializedName("chatRoomId")
    val chatRoomId: Long,

    @SerializedName("characterFirstName")
    val characterFirstName: String,

    @SerializedName("characterImageUrl")
    val characterImageUrl: String,

    @SerializedName("isMain")
    val isMain: Boolean,

    @SerializedName("isMuted")
    val isMuted: Boolean,

    @SerializedName("lastMessage")
    val lastMessage: String,

    @SerializedName("lastMessageAt")
    val lastMessageAt: String,

    @SerializedName("unreadCount")
    val unreadCount: Int
)
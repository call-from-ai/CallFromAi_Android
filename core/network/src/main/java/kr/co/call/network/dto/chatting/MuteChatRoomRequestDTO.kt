package kr.co.call.network.dto.chatting

import com.google.gson.annotations.SerializedName

data class MuteChatRoomRequestDTO(
    @SerializedName("isMuted")
    val isMuted: Boolean
)

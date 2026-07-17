package kr.co.call.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ChattingNavKey: NavKey

@Serializable
data object ChatListNavKey: NavKey

@Serializable
data class ChatRoomNavKey(
    val roomId: Long,
): NavKey
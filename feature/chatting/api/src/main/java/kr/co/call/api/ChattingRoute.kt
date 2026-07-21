package kr.co.call.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ChattingNavKey: NavKey

@Serializable
data class ChatRoomNavKey(
    val roomId: Long,
    val name: String,
): NavKey
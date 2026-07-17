package kr.co.call.impl.model

sealed interface ChatListIntent {
    data class ClickChatRoom(val roomId: Long) : ChatListIntent
}
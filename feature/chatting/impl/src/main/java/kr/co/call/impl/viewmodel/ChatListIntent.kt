package kr.co.call.impl.viewmodel

sealed interface ChatListIntent {
    data class ClickChatRoom(val roomId: Long, val name: String) : ChatListIntent
}
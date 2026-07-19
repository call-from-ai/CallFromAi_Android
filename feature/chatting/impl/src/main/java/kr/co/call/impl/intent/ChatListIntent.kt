package kr.co.call.impl.intent

sealed interface ChatListIntent {
    data class ClickChatRoom(val roomId: Long) : ChatListIntent

    data object ClickManagerChatRoom : ChatListIntent
}
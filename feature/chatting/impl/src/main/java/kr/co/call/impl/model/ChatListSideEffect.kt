package kr.co.call.impl.model

sealed interface ChatListSideEffect {
    data class NavigateToChatRoom(val roomId: Long): ChatListSideEffect
}
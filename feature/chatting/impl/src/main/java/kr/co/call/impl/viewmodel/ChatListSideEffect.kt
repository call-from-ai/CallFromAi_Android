package kr.co.call.impl.viewmodel

sealed interface ChatListSideEffect {
    data class NavigateToChatRoom(val roomId: Long): ChatListSideEffect
}
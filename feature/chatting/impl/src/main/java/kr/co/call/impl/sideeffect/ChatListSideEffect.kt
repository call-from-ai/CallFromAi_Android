package kr.co.call.impl.sideeffect

sealed interface ChatListSideEffect {
    data class NavigateToChatRoom(val roomId: Long): ChatListSideEffect
    data object NavigateToManagerChatRoom: ChatListSideEffect
}
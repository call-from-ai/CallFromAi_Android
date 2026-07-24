package kr.co.call.impl.sideeffect

sealed interface ChatListSideEffect {
    data class NavigateToChatRoom(val roomId: Long): ChatListSideEffect
    data object NavigateToManagerChatRoom: ChatListSideEffect

    data class UpdateAlarmSetting(val roomId: Long): ChatListSideEffect
    data class DeleteChatRoom(val roomId: Long): ChatListSideEffect
}
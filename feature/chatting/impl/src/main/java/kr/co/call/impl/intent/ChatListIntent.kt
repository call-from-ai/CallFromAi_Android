package kr.co.call.impl.intent

sealed interface ChatListIntent {
    data class ClickChatRoom(val roomId: Long) : ChatListIntent

    data object ClickManagerChatRoom : ChatListIntent

    data class UpdateAlarmSetting(val roomId: Long) : ChatListIntent

    data class ClickDeleteChatRoom(val roomId: Long) : ChatListIntent

    data class DeleteChatRoom(val roomId: Long) : ChatListIntent

    data object DismissDeleteDialog : ChatListIntent
}
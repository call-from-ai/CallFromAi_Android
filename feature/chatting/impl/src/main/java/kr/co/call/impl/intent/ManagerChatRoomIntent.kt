package kr.co.call.impl.intent

sealed interface ManagerChatRoomIntent {
    data object ClickWhenCall: ManagerChatRoomIntent
    data object ClickChangePartnerInfo: ManagerChatRoomIntent
    data object ClickUpdateInfo: ManagerChatRoomIntent
    data object ClickAskToAgent: ManagerChatRoomIntent
}

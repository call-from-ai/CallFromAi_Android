package kr.co.call.impl.state

import kr.co.call.impl.model.ManagerChatUiItem

data class ManagerChatRoomUiState(
    val chatItems: List<ManagerChatUiItem> = emptyList(),
)
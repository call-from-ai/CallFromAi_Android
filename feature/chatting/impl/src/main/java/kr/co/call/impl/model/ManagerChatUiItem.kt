package kr.co.call.impl.model

import kr.co.call.domain.model.chatting.ManagerChatItem
import kr.co.call.domain.util.LoadStatus

data class ManagerChatUiItem(
    val message: ManagerChatItem,
    val loadStatus: LoadStatus = LoadStatus.Idle,
    val time: String = "",
)
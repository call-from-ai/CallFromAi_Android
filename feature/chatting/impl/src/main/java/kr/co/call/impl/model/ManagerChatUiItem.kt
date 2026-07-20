package kr.co.call.impl.model

import java.time.LocalDate
import kr.co.call.domain.model.chatting.ManagerChatItem
import kr.co.call.domain.util.LoadStatus

sealed class ManagerChatUiItem {

    data class Message(
        val message: ManagerChatItem,
        val loadStatus: LoadStatus = LoadStatus.Idle,
        val time: String = "",
    ) : ManagerChatUiItem()

    data class DateSeparator(val date: LocalDate) : ManagerChatUiItem()
}

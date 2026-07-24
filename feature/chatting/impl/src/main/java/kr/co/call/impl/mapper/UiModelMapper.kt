package kr.co.call.impl.mapper

import kr.co.call.domain.model.chatting.ChatItem
import kr.co.call.domain.model.chatting.ManagerChatItem
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.model.ChatItemUiModel
import kr.co.call.impl.model.ManagerChatUiItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val dateSeparatorFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")

object UiModelMapper {

    fun ManagerChatItem.toUiItem(
        loadStatus: LoadStatus = LoadStatus.Idle,
    ): ManagerChatUiItem.Message =
        ManagerChatUiItem.Message(
            message = this,
            loadStatus = loadStatus,
            time = this.createdAt.format(timeFormatter),
        )

    fun ChatItem.Message.toUiItem(
        loadStatus: LoadStatus = LoadStatus.Idle,
    ): ChatItemUiModel.Message =
        ChatItemUiModel.Message(
            chatMessageId = this.chatMessageId,
            senderType = this.senderType,
            content = this.content,
            messageType = this.messageType,
            photoUrl = this.photoUrl,
            time = this.createdTime.format(timeFormatter),
            loadStatus = loadStatus,
        )

    fun ChatItem.DateSeparator.toUiItem(): ChatItemUiModel.DateSeparator =
        ChatItemUiModel.DateSeparator(
            date = this.date.format(dateSeparatorFormatter)
        )

    fun ChatItem.toUiItem(): ChatItemUiModel = when (this) {
        is ChatItem.Message -> toUiItem()
        is ChatItem.DateSeparator -> toUiItem()
    }

}

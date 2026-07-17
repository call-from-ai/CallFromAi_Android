package kr.co.call.impl.mapper

import kr.co.call.domain.model.chatting.ChatMessage
import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType
import kr.co.call.impl.model.ChatItemUiModel
import kr.co.call.impl.model.SendStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

object UiModelMapper {
    /** 도메인 메시지 → 말풍선 UI 모델. 항상 SENT. */
    fun ChatMessage.toUiModel(): ChatItemUiModel =
        ChatItemUiModel(
            clientId = "server-$chatMessageId",
            chatMessageId = chatMessageId,
            senderType = senderType,
            content = content,
            messageType = messageType,
            photoUrl = photoUrl.ifEmpty { null },
            time = createdTime,
            sendStatus = SendStatus.SENT,
        )

    /** 유저가 방금 보낸(서버 미확정) 로컬 메시지. */
    fun createSendingMessage(content: String, photoUrl: String? = null): ChatItemUiModel =
        ChatItemUiModel(
            clientId = UUID.randomUUID().toString(),
            chatMessageId = null,
            senderType = SenderType.USER,
            content = content,
            messageType = if (photoUrl != null) MessageType.TEXT_IMAGE else MessageType.TEXT,
            photoUrl = photoUrl,
            time = LocalDateTime.now().format(timeFormatter),
            sendStatus = SendStatus.SENDING,
        )

}

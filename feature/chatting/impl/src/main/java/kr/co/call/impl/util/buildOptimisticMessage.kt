package kr.co.call.impl.util

import android.net.Uri
import kr.co.call.domain.model.chatting.ImageData
import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.model.ChatItemUiModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * 서버에 메시지가 성공적으로 전송되기 전에 UI에 표시할 낙관적 [ChatItemUiModel.Message]를 생성합니다.
 *
 * 전송 중 상태를 가진 임시 메시지를 생성하며, 저장된 메시지와 구분하기 위한 고유 클라이언트 ID와
 * 음수 ID를 사용합니다.
 *
 * @param message 메시지의 텍스트 내용입니다.
 * @param image 메시지 유형([MessageType])을 결정하는 데 사용되는 이미지 데이터입니다.
 * @param imageUri UI에서 미리보기로 표시할 로컬 [Uri] 이미지 경로입니다.
 */
fun buildOptimisticMessage(
    message: String?,
    image: ImageData?,
    imageUri: Uri?,
): ChatItemUiModel.Message {
    val messageType = when {
        !message.isNullOrBlank() && image != null -> MessageType.TEXT_IMAGE
        image != null -> MessageType.IMAGE
        else -> MessageType.TEXT
    }

    return ChatItemUiModel.Message(
        chatMessageId = -System.nanoTime(),
        clientId = UUID.randomUUID().toString(),
        senderType = SenderType.USER,
        content = message ?: "",
        messageType = messageType,
        photoUrl = imageUri?.toString() ?: "",
        time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
        loadStatus = LoadStatus.Loading,
    )
}

package kr.co.call.data.mapper

import kr.co.call.domain.model.chatting.ChatHeader
import kr.co.call.domain.model.chatting.ChatItem
import kr.co.call.domain.model.chatting.ChatSseEvent
import kr.co.call.domain.model.chatting.ChatSseMessage
import kr.co.call.domain.model.chatting.ChatSummary
import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType
import kr.co.call.network.dto.chatting.ChatHeaderDTO
import kr.co.call.network.dto.chatting.ChatMessageDTO
import kr.co.call.network.dto.chatting.ChatMessagesDTO
import kr.co.call.network.dto.chatting.ChatRoomDTO
import kr.co.call.network.dto.chatting.ChatRoomsDTO
import kr.co.call.network.dto.chatting.ChatSseNetworkEvent
import kr.co.call.core.common.util.TimeUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 채팅 관련 네트워크 DTO를 Domain 모델로 변환하는 매퍼 객체입니다.
 *
 * 각 함수는 확장 함수 형태로 정의되어 있으며,
 * [ChatHeaderDTO], [ChatRoomDTO], [ChatRoomsDTO], [ChatMessageDTO],
 * [ChatMessagesDTO], [ChatSseNetworkEvent]를 대응하는 Domain 모델로 변환합니다.
 *
 * - String 타입의 `senderType`, `messageType`은 각 enum의 [fromApiValue]를 통해 안전하게 변환됩니다.
 * - `createdAt` 문자열은 [DateTimeFormatter.ISO_LOCAL_DATE_TIME] 포맷으로 파싱됩니다.
 * - `isMuted`는 동일 필드명으로 직접 매핑됩니다.
 */
object ChatMapper {

    // 채팅방 헤더 DTO를 Domain 모델로 변환
    fun ChatHeaderDTO.toDomain(): ChatHeader = ChatHeader(
        characterId = characterId,
        characterFirstName = characterFirstName,
        characterImageUrl = characterImageUrl,
        dDay = dDay,
        isMain = isMain,
    )

    // 채팅방 단건 DTO를 채팅 목록 요약 Domain 모델로 변환
    fun ChatRoomDTO.toDomain(): ChatSummary = ChatSummary(
        chatRoomId = chatRoomId,
        image = characterImageUrl,
        name = characterFirstName,
        isMainCharacter = isMain,
        content = lastMessage ?: "",
        whenSubmitted = lastMessageAt?.let { TimeUtil.toTimeAgoText(it) } ?: "",
        unReadMessageCount = unreadCount.toString(),
        isMuted = isMuted,
    )

    // 채팅방 목록 DTO를 채팅 요약 Domain 모델 리스트로 변환
    fun ChatRoomsDTO.toDomain(): List<ChatSummary> = content.map { it.toDomain() }

    // 채팅 메시지 단건 DTO를 Domain 메시지 모델로 변환
    fun ChatMessageDTO.toDomain(): ChatItem.Message = ChatItem.Message(
        chatMessageId = chatMessageId,
        senderType = SenderType.fromApiValue(senderType),
        content = content.orEmpty(),
        messageType = MessageType.fromApiValue(messageType),
        photoUrl = photoUrl.orEmpty(),
        createdTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    )

    // 채팅 메시지 목록 DTO를 Domain 메시지 모델 리스트로 변환
    fun ChatMessagesDTO.toDomain(): List<ChatItem.Message> = content.map { it.toDomain() }

    // SSE 네트워크 이벤트를 Domain 이벤트로 변환
    fun ChatSseNetworkEvent.toDomain(): ChatSseEvent = when (this) {
        ChatSseNetworkEvent.Connected -> ChatSseEvent.Connected
        is ChatSseNetworkEvent.Loading -> ChatSseEvent.Loading(chatRoomId)
        is ChatSseNetworkEvent.Failed -> ChatSseEvent.Failed(chatRoomId)
        is ChatSseNetworkEvent.Message -> ChatSseEvent.Message(
            ChatSseMessage(
                chatRoomId = chatRoomId,
                chatMessageId = chatMessageId,
                senderType = SenderType.fromApiValue(senderType),
                content = content,
                messageType = MessageType.fromApiValue(messageType),
                createdAt = createdAt,
            )
        )
    }
}

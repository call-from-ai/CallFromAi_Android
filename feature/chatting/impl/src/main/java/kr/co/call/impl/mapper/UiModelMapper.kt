package kr.co.call.impl.mapper

import kr.co.call.domain.model.chatting.ChatHeader
import kr.co.call.domain.model.chatting.ChatItem
import kr.co.call.domain.model.chatting.ChatSseMessage
import kr.co.call.domain.model.chatting.ManagerChatItem
import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.model.ChatItemUiModel
import kr.co.call.impl.model.ManagerChatUiItem
import kr.co.call.impl.model.TopHeader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * 도메인 계층 모델을 UI 계층에서 사용하는 모델로 변환하는 Mapper 객체입니다.
 *
 * 이 Mapper는 [ChatItem], [ManagerChatItem], [ChatHeader]와 같은 채팅 관련
 * 도메인 엔티티를 각각의 UI 모델로 변환하며, 화면에 표시할 수 있도록
 * 시간, 날짜, 로딩 상태 등의 값을 적절한 형식으로 가공합니다.
 */
object UiModelMapper {

    // 매니저 채팅 메시지를 UI 모델로 변환
    fun ManagerChatItem.toUiItem(
        loadStatus: LoadStatus = LoadStatus.Idle,
    ): ManagerChatUiItem.Message =
        ManagerChatUiItem.Message(
            message = this,
            loadStatus = loadStatus,
            time = this.createdAt.format(timeFormatter),
        )

    // 일반 채팅 메시지를 UI 모델로 변환
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

    // 날짜 구분선을 UI 모델로 변환
    fun ChatItem.DateSeparator.toUiItem(): ChatItemUiModel.DateSeparator =
        ChatItemUiModel.DateSeparator(
            date = this.date.format(dateSeparatorFormatter)
        )

    // 채팅 아이템을 UI 모델로 변환
    fun ChatItem.toUiItem(): ChatItemUiModel = when (this) {
        is ChatItem.Message -> toUiItem()
        is ChatItem.DateSeparator -> toUiItem()
    }

    // SSE로 수신한 메시지를 UI 모델로 변환 (createdAt 문자열을 HH:mm 포맷으로 파싱)
    fun ChatSseMessage.toUiItem(): ChatItemUiModel.Message = ChatItemUiModel.Message(
        chatMessageId = this.chatMessageId,
        senderType = this.senderType,
        content = this.content,
        messageType = this.messageType,
        time = runCatching {
            LocalDateTime.parse(this.createdAt).format(timeFormatter)
        }.getOrDefault(""),
        loadStatus = LoadStatus.Idle,
    )

    // SSE Loading 이벤트에 대응하는 로딩 버블 생성
    fun sseLoadingBubble(clientId: String): ChatItemUiModel.Message = ChatItemUiModel.Message(
        chatMessageId = -1L,
        clientId = clientId,
        senderType = SenderType.AI,
        messageType = MessageType.TEXT,
        loadStatus = LoadStatus.Loading,
    )

    // 채팅방 상단 정보를 UI 모델로 변환
    fun ChatHeader.toUiItem(): TopHeader = TopHeader(
        characterId = this.characterId,
        name = this.characterFirstName,
        imgUrl = this.characterImageUrl,
        dDay = "D+ ${this.dDay}",
        isMain = this.isMain,
    )
}

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val dateSeparatorFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")

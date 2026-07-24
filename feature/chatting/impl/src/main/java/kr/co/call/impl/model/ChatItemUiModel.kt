package kr.co.call.impl.model

import android.net.Uri
import kr.co.call.domain.model.chatting.ManagerChatItem
import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType
import kr.co.call.domain.util.LoadStatus

/**
 * 채팅 메시지 목록에 표시되는 다양한 아이템을 나타내는 UI 모델입니다.
 *
 * 이 sealed interface는 채팅 화면에 나타날 수 있는 개별 메시지, 날짜 구분선,
 * 그리고 채팅방의 상단 헤더 정보와 같이 서로 다른 유형의 뷰(Heterogeneous list)들을
 * 하나의 리스트에서 처리할 수 있도록 정의합니다.
 */
sealed interface ChatItemUiModel {

    data class Message(
        val chatMessageId: Long,
        val senderType: SenderType,
        val content: String = "",
        val messageType: MessageType,
        val photoUrl: String = "",
        val time: String = "",
        val loadStatus: LoadStatus = LoadStatus.Idle,
    ) : ChatItemUiModel

    data class DateSeparator(val date: String) : ChatItemUiModel
}

data class TopHeader(
    val imgUrl: String = "",
    val name: String = "",
    val dDay: String = "",
    val characterId: Long = -1,
): ChatItemUiModel

data class TextFieldState(
    val text: String = "",
    val selectedImage: Uri? = null,
)
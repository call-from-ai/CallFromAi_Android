package kr.co.call.impl.model

import android.net.Uri
import kr.co.call.domain.model.chatting.ManagerChatItem
import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType
import kr.co.call.domain.util.LoadStatus
import java.util.UUID

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
        val clientId: String = UUID.randomUUID().toString(),
        val senderType: SenderType,
        val content: String = "",
        val messageType: MessageType,
        val photoUrl: String = "",
        val time: String = "",
        val loadStatus: LoadStatus = LoadStatus.Idle,
    ) : ChatItemUiModel

    data class DateSeparator(val date: String) : ChatItemUiModel
}

/**
 * 채팅방 상단에 표시되는 캐릭터의 기본 정보를 나타내는 UI 모델입니다.
 *
 * @property characterId 캐릭터의 고유 식별자
 * @property name 캐릭터의 이름
 * @property imgUrl 캐릭터의 프로필 이미지 URL
 * @property dDay 캐릭터와의 관계를 나타내는 디데이 정보
 */
data class TopHeader(
    val characterId: Long = -1,
    val name: String = "",
    val imgUrl: String = "",
    val dDay: String = "",
): ChatItemUiModel

/**
 * 채팅 입력창의 상태를 나타내는 UI 모델입니다.
 *
 * 사용자가 입력 중인 텍스트와 전송을 위해 선택된 이미지 정보를 포함하며,
 * 채팅 리스트의 하단 입력 인터페이스 상태를 관리하는 데 사용됩니다.
 *
 * @property text 현재 입력된 메시지 텍스트
 * @property selectedImage 전송을 위해 선택된 이미지의 URI (없을 경우 null)
 */
data class TextFieldState(
    val text: String = "",
    val selectedImage: Uri? = null,
): ChatItemUiModel
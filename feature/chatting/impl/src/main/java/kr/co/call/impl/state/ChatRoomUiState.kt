package kr.co.call.impl.state

import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.model.ChatItemUiModel
import kr.co.call.impl.model.TextFieldState
import kr.co.call.impl.model.TopHeader

/**
 * 채팅방 화면의 UI 상태를 관리하는 데이터 클래스입니다.
 *
 * @property topHeader 상단 헤더(제목, 버튼 등)의 UI 상태입니다.
 * @property chatItems 화면에 표시될 채팅 메시지 및 항목들의 리스트입니다.
 * @property textFieldState 메시지 입력창의 UI 상태입니다.
 * @property status 채팅방의 데이터 로딩 및 요청 처리 상태입니다.
 * @property deletedIds 삭제 처리된 메시지 ID 집합입니다. 삭제된 메시지는 화면에서 제외하는 데 사용됩니다.
 * @property showDeleteChatRoomDialog 채팅방 삭제 확인 다이얼로그의 표시 여부입니다.
 * @property expandedProfileUrl 현재 확대하여 표시 중인 프로필 이미지 URL입니다.
 * @property selectedMessageId 현재 선택된 메시지 ID입니다.
 */
data class ChatRoomUiState(
    val topHeader: TopHeader = TopHeader(),
    val chatItems: List<ChatItemUiModel> = emptyList(),
    val textFieldState: TextFieldState = TextFieldState(),
    val status: LoadStatus = LoadStatus.Idle,
    val deletedIds: Set<Long> = emptySet(),
    val showDeleteChatRoomDialog: Boolean = false,
    val expandedProfileUrl: String? = null,
    val selectedMessageId: Long? = null
)

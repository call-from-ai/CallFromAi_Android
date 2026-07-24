package kr.co.call.impl.state

import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.model.ChatItemUiModel
import kr.co.call.impl.model.TextFieldState
import kr.co.call.impl.model.TopHeader

/**
 * 채팅방 화면의 UI 상태를 나타냅니다.
 *
 * @property chatItems 대화에 표시될 채팅 메시지와 목록입니다.
 * @property status 채팅방 데이터의 현재 로딩 상태입니다. (예: Idle, Loading, Error)
 * @property textFieldState 현재 입력 중인 메시지와 관련 상태를 포함하는 입력창의 상태입니다.
 */
data class ChatRoomUiState(
    val topHeader: TopHeader = TopHeader(),
    val chatItems: List<ChatItemUiModel> = emptyList(),
    val textFieldState: TextFieldState = TextFieldState(),
    val status: LoadStatus = LoadStatus.Idle,
)

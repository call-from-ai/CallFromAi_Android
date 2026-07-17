package kr.co.call.impl.viewmodel

import android.net.Uri
import kr.co.call.domain.model.chatting.ChatMessageList
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.model.ChatItemUiModel

/**
 * 채팅방 화면 전체의 UI 상태.
 *
 * @property messages 화면에 표시할 말풍선 목록. 서버 메시지와 전송 중인 로컬 메시지를 함께 담는다.
 *   최신이 마지막 원소이며, LazyColumn은 reverseLayout으로 그린다.
 * @property nextCursor 다음 과거 메시지를 불러올 커서. null이면 더 없음.
 * @property hasNext 위로 스크롤 시 불러올 과거 메시지가 남아있는지 여부.
 * @property initialLoadStatus 방 첫 진입 시의 로딩 상태(풀스크린 로딩/에러용).
 * @property isLoadingMore 과거 메시지 추가 로딩 여부(상단 프로그레스용).
 * @property textFieldState 하단 입력창 상태.
 */
data class ChatRoomUiState(
    val messages: List<ChatItemUiModel> = emptyList(),
    val nextCursor: Long? = null,
    val hasNext: Boolean = true,
    val initialLoadStatus: LoadStatus = LoadStatus.Idle,
    val isLoadingMore: Boolean = false,
    val textFieldState: TextFieldState = TextFieldState(),
)

/**
 * 하단 메시지 입력창의 상태.
 *
 * @property text 입력 중인 텍스트.
 * @property selectedImage 첨부하려고 선택한 이미지 Uri. 없으면 null.
 * @property loadStatus 선택 이미지의 업로드 진행 상태.
 */
data class TextFieldState(
    val text: String = "",
    val selectedImage: Uri? = null,
    val loadStatus: LoadStatus = LoadStatus.Idle
)


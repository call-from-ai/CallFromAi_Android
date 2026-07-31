package kr.co.call.impl.intent

import android.net.Uri
import kr.co.call.domain.model.chatting.ImageData

/**
 * 채팅방 화면에서 발생하는 모든 사용자 인터랙션 및 이벤트를 정의하는 Sealed Interface입니다.
 * MVI(Model-View-Intent) 패턴의 Intent 단계에서 사용되며, UI의 상태 변화를 유도하는 모든 동작을 포함합니다.
 *
 * 주요 기능 범위:
 * - 메시지 전송 및 관리 (텍스트, 이미지, 삭제, 롱프레스)
 * - 미디어 처리 (카메라 촬영, 갤러리 선택, 이미지 취소)
 * - 통화 기능 (통화 연결 및 진입)
 * - UI 상태 제어 (다이얼로그 및 팝업 닫기, 프로필 크게 보기)
 */
sealed interface ChatRoomIntent {

    // 메시지(텍스트/이미지)를 전송하는 Intent
    data class SendMessage(
        val message: String? = null,
        val image: ImageData? = null,
        val imageUri: Uri? = null,
    ) : ChatRoomIntent

    // 통화 버튼 클릭을 처리하는 Intent
    data class ClickCall(
        val characterId: Long,
    ): ChatRoomIntent

    // 삭제 확인 다이얼로그를 닫는 Intent
    data object DismissDeleteDialog: ChatRoomIntent

    // 통화 화면으로 이동하는 Intent
    data class GoToCall(
        val characterId: Long,
    ): ChatRoomIntent

    // 카메라 실행 버튼 클릭을 처리하는 Intent
    data object ClickCamera: ChatRoomIntent

    // 갤러리 실행 버튼 클릭을 처리하는 Intent
    data object ClickGallery: ChatRoomIntent

    // 메시지 롱프레스 이벤트를 처리하는 Intent
    data class LongPressMessage(
        val messageId: Long,
    ): ChatRoomIntent

    // 선택한 메시지를 삭제하는 Intent
    data class DeleteMessage(
        val messageId: Long
    ): ChatRoomIntent

    // 갤러리에서 선택한 이미지를 처리하는 Intent
    data class ImagesPicked(val uri: Uri) : ChatRoomIntent

    // 카메라로 촬영한 이미지를 처리하는 Intent
    data class PictureTaken(val uri: Uri) : ChatRoomIntent

    // 선택된 이미지를 입력창에서 제거하는 Intent
    data object CancelImage: ChatRoomIntent

    // 프로필 이미지 확대를 요청하는 Intent
    data class ClickProfile(val imageUrl: String) : ChatRoomIntent

    // 확대된 프로필 이미지를 닫는 Intent
    data object DismissProfile : ChatRoomIntent

    // 메시지 액션 팝업을 닫는 Intent
    data object DismissPopup : ChatRoomIntent

    // 메인 캐릭터가 아닙니다 다이얼로그를 표시하는 Intent
    data object ShowNotMainDialog : ChatRoomIntent

    // 메인 캐릭터가 아닙니다 다이얼로그를 닫는 Intent
    data object DismissNotMainDialog : ChatRoomIntent
}
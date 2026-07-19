package kr.co.call.domain.usecase.chatting

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kr.co.call.domain.model.chatting.ManagerChatItem
import kr.co.call.domain.model.chatting.UpdateInfoMessage
import kr.co.call.domain.model.chatting.UpdateInfoMessageType
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 사용자가 "기록한 정보를 수정하고 싶어요" 프롬프트를 선택했을 때
 * 매니저가 제공하는 내 프로필 정보 수정 안내 메시지를 제공합니다.
 */
class WantToUpdateRecordUseCase @Inject constructor() {

    /**
     * 기록한 정보(내 프로필) 수정 관련 안내 메시지를 반환합니다.
     *
     * @return 기록 정보 수정 안내 메시지 스트림
     */
    operator fun invoke(): Flow<ManagerChatItem> = flow {
        val now = LocalDateTime.now()

        emit(
            UpdateInfoMessage(
                id = "update_record",
                content = "기록한 정보는 마이페이지 > 내 프로필에서\n수정할 수 있어요. 📝",
                type = UpdateInfoMessageType.NORMAL,
                createdAt = now
            )
        )

        emit(
            UpdateInfoMessage(
                id = "update_record_apply",
                content = "수정한 정보는\n이후 대화에 자연스럽게 반영 돼요!",
                type = UpdateInfoMessageType.APPLIED_NATURALLY,
                createdAt = now
            )
        )
    }
}
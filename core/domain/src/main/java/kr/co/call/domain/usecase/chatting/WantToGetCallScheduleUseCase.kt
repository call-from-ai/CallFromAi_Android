package kr.co.call.domain.usecase.chatting

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kr.co.call.domain.model.chatting.ManagerChatItem
import kr.co.call.domain.model.chatting.WhenCallMessage
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 사용자가 "전화는 언제 오나요?" 프롬프트를 선택했을 때
 * 매니저가 제공하는 전화 예정 시간 안내 메시지를 제공합니다.
 */
class WantToGetCallScheduleUseCase @Inject constructor() {

    /**
     * 전화 예정 시간 관련 매니저 메시지를 반환합니다.
     *
     * @return 전화 일정 안내 메시지 스트림
     */
    operator fun invoke(): Flow<ManagerChatItem> = flow {
        val now = LocalDateTime.now()

        emit(
            WhenCallMessage(
                id = "call_schedule",
                content = "설정한 시간대를 기준으로 먼저 전화가 와요 ☎️",
                createdAt = now
            )
        )

        emit(
            WhenCallMessage(
                id = "call_schedule_setting",
                content = "원하는 시간은 마이페이지 > 연락시간 설정에서\n언제든지 변경할 수 있어요.",
                createdAt = now
            )
        )
    }
}
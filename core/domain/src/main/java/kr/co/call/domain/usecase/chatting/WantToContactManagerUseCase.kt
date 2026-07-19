package kr.co.call.domain.usecase.chatting

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kr.co.call.domain.model.chatting.AskToAgentMessage
import kr.co.call.domain.model.chatting.ManagerChatItem
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 사용자가 "상담원에게 문의하기" 프롬프트를 선택했을 때
 * 매니저가 제공하는 안내 메시지를 제공합니다.
 */
class WantToContactManagerUseCase @Inject constructor() {

    /**
     * 상담원 문의 관련 안내 메시지를 반환합니다.
     *
     * @return 상담원 문의 안내 메시지 스트림
     */
    operator fun invoke(): Flow<ManagerChatItem> = flow {
        val now = LocalDateTime.now()

        emit(
            AskToAgentMessage(
                id = "ask_to_agent",
                content = "상담원 문의하기 기능은 준비중이에요.",
                createdAt = now
            )
        )
    }
}
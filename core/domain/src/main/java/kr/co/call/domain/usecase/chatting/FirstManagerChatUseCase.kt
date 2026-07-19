package kr.co.call.domain.usecase.chatting

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kr.co.call.domain.model.chatting.ManagerChatItem
import kr.co.call.domain.model.chatting.ManagerFirstMessage
import kr.co.call.domain.model.chatting.ManagerFirstMessageType
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * 매니저 채팅방에 처음 진입했을 때 표시할 초기 메시지 목록을
 * 순차적으로 방출하는 유스케이스입니다.
 *
 * 메시지의 표시 간격이나 로딩 연출은 담당하지 않으며,
 * 초기에 노출해야 할 메시지 데이터만 제공합니다.
 */
class FirstManagerChatUseCase @Inject constructor() {

    /**
     * 초기 매니저 메시지를 [Flow] 형태로 순차 방출합니다.
     *
     * @return 초기 매니저 채팅 메시지 스트림
     */
    operator fun invoke(): Flow<ManagerChatItem> = flow {
        val now = LocalDateTime.now()

        emit(
            ManagerFirstMessage(
                id = "welcome",
                content = "반가워요! 👋🏻 전화왔어 매니저에요!",
                type = ManagerFirstMessageType.NORMAL,
                createdAt = now
            )
        )

        emit(
            ManagerFirstMessage(
                id = "intro",
                content = "전화왔어는 AI가 먼저 연락하고,\n사용자를 기억하며 관계를 만들어가는 서비스예요.",
                type = ManagerFirstMessageType.NORMAL,
                createdAt = now
            )
        )

        emit(
            ManagerFirstMessage(
                id = "relationship",
                content = "대화를 많이 나눌수록,\n더 자연스러운 관계가 생성돼요.",
                type = ManagerFirstMessageType.RELATIONSHIP,
                createdAt = now
            )
        )

        emit(
            ManagerFirstMessage(
                id = "guide",
                content = "필요한 안내는 언제든 이 채팅방에서 확인해주세요!",
                type = ManagerFirstMessageType.NORMAL,
                createdAt = now
            )
        )
    }
}
package kr.co.call.domain.usecase.chatting

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kr.co.call.domain.model.chatting.ManagerFirstMessage
import kr.co.call.domain.model.chatting.ManagerFirstMessageType
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * 사용자가 매니저 채팅을 처음 시작할 때 매니저(AI)가 보내는 초기 메시지를
 * 순차적으로 방출하는 유스케이스입니다.
 */
class FirstManagerChatUseCase @Inject constructor() {

    operator fun invoke(): Flow<ManagerFirstMessage> = flow {
        val now = LocalDateTime.now()

        val messages = listOf(
            ManagerFirstMessage(
                id = "welcome",
                content = "수현님, 반가워요! 👋🏻",
                type = ManagerFirstMessageType.NORMAL,
                createdAt = now
            ),
            ManagerFirstMessage(
                id = "intro",
                content = "전화왔어는 AI가 먼저 연락하고,\n사용자를 기억하며 관계를 만들어가는 서비스예요.",
                type = ManagerFirstMessageType.NORMAL,
                createdAt = now
            ),
            ManagerFirstMessage(
                id = "relationship",
                content = "대화를 많이 나눌수록,\n더 자연스러운 관계가 생성돼요.",
                type = ManagerFirstMessageType.RELATIONSHIP,
                createdAt = now
            ),
            ManagerFirstMessage(
                id = "guide",
                content = "필요한 안내는 언제든 이 채팅방에서 확인해주세요!",
                type = ManagerFirstMessageType.NORMAL,
                createdAt = now
            )
        )

        messages.forEachIndexed { index, message ->
            if (index != 0) {
                delay(1500.milliseconds)
            }
            emit(message)
        }
    }
}
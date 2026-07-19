package kr.co.call.domain.usecase.chatting

import kr.co.call.domain.model.chatting.ManagerFirstMessage
import kr.co.call.domain.model.chatting.ManagerFirstMessageType
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 사용자가 매니저 채팅을 처음 시작할 때 매니저(AI)가 보내는 초기 메시지 세트를 제공하는 유스케이스입니다.
 *
 * 환영 인사, 서비스 소개, 관계 형성 설명 및 일반적인 안내 사항을 포함하는
 * 미리 정의된 메시지 시퀀스를 반환합니다.
 *
 * @return 매니저의 오프닝 대화를 나타내는 [ManagerFirstMessage] 리스트.
 */
class FirstManagerChatUseCase @Inject constructor() {
    suspend operator fun invoke(): List<ManagerFirstMessage> {
        val now = LocalDateTime.now()

        return listOf(
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
    }

}
package kr.co.call.domain.usecase.chatting

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kr.co.call.domain.model.chatting.ChangePartnerInfoMessage
import kr.co.call.domain.model.chatting.ChangePartnerInfoMessageType
import kr.co.call.domain.model.chatting.ManagerChatItem
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * 사용자가 "상대방 정보를 수정하고 싶어요" 프롬프트를 선택했을 때
 * 매니저가 제공하는 이상형 정보 수정 안내 메시지를 제공합니다.
 */
class WantToUpdatePartnerInfoUseCase @Inject constructor() {

    /**
     * 상대방(이상형) 정보 수정 관련 안내 메시지를 반환합니다.
     *
     * @return 이상형 정보 수정 안내 메시지 스트림
     */
    operator fun invoke(): Flow<ManagerChatItem> = flow {
        val now = LocalDateTime.now()

        emit(
            ChangePartnerInfoMessage(
                id = UUID.randomUUID().toString(),
                content = "상대방 정보는 마이페이지 > 이상형 정보 수정에서\n변경할 수 있어요. 👤",
                type = ChangePartnerInfoMessageType.NORMAL,
                createdAt = now
            )
        )

        emit(
            ChangePartnerInfoMessage(
                id = UUID.randomUUID().toString(),
                content = "이름, 성격, 관계 설정까지 언제든 수정 가능해요!",
                type = ChangePartnerInfoMessageType.NORMAL,
                createdAt = now
            )
        )

        emit(
            ChangePartnerInfoMessage(
                id = UUID.randomUUID().toString(),
                content = "변경한 설정은\n다음 대화와 전화부터 바로 반영 돼요.",
                type = ChangePartnerInfoMessageType.NEXT_CONVERSATION,
                createdAt = now
            )
        )
    }
}
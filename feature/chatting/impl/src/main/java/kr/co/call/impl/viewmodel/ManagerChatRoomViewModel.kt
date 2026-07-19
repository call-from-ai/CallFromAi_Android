package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kr.co.call.domain.model.chatting.ManagerChatItem
import kr.co.call.domain.model.chatting.UserMessage
import java.time.LocalDateTime
import java.util.UUID
import kr.co.call.domain.usecase.chatting.FirstManagerChatUseCase
import kr.co.call.domain.usecase.chatting.WantToContactManagerUseCase
import kr.co.call.domain.usecase.chatting.WantToGetCallScheduleUseCase
import kr.co.call.domain.usecase.chatting.WantToUpdatePartnerInfoUseCase
import kr.co.call.domain.usecase.chatting.WantToUpdateRecordUseCase
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.intent.ManagerChatRoomIntent
import kr.co.call.impl.mapper.UiModelMapper.toUiItem
import kr.co.call.impl.state.ManagerChatRoomUiState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ManagerChatRoomViewModel @Inject constructor(
    // 각 유스케이스들 주입
    private val firstManagerChatUseCase: FirstManagerChatUseCase,
    private val wantToGetCallScheduleUseCase: WantToGetCallScheduleUseCase,
    private val wantToUpdatePartnerInfoUseCase: WantToUpdatePartnerInfoUseCase,
    private val wantToUpdateRecordUseCase: WantToUpdateRecordUseCase,
    private val wantToContactManagerUseCase: WantToContactManagerUseCase
): ViewModel(), ContainerHost<ManagerChatRoomUiState, Nothing> {

    override val container: Container<ManagerChatRoomUiState, Nothing> = container(
        initialState = ManagerChatRoomUiState()
    )

    init {
        intent {
            appendManagerMessages(firstManagerChatUseCase())
        }
    }

    fun handleIntent(intent: ManagerChatRoomIntent) {
        when (intent) {
            ManagerChatRoomIntent.ClickWhenCall ->
                sendUserMessageThen("전화는 언제 오나요?", wantToGetCallScheduleUseCase())
            ManagerChatRoomIntent.ClickChangePartnerInfo ->
                sendUserMessageThen("상대방 정보를 변경하고 싶어요.", wantToUpdatePartnerInfoUseCase())
            ManagerChatRoomIntent.ClickUpdateInfo ->
                sendUserMessageThen("기록한 정보를 수정하고 싶어요.", wantToUpdateRecordUseCase())
            ManagerChatRoomIntent.ClickAskToAgent ->
                sendUserMessageThen("상담원에게 문의하기", wantToContactManagerUseCase())
        }
    }

    private fun sendUserMessageThen(content: String, flow: Flow<ManagerChatItem>) = intent {
        val userMessage = UserMessage(
            id = UUID.randomUUID().toString(), // UI 상태에서 메시지를 구분하기 위한 임시 ID 생성
            content = content,
            createdAt = LocalDateTime.now()
        )
        reduce {
            state.copy(chatItems = state.chatItems + userMessage.toUiItem())
        }
        appendManagerMessages(flow)
    }

    // 메시지를 로딩 상태로 추가한 뒤, 일정 시간 후 실제 내용을 표시하는 공통 로직
    //
    // intent 블록의 리시버가 Syntax<STATE, SIDE_EFFECT>이고 reduce가 그 멤버이므로,
    // 이 함수도 Syntax 확장 함수로 선언해야 내부에서 reduce를 호출할 수 있다.
    private suspend fun Syntax<ManagerChatRoomUiState, Nothing>.appendManagerMessages(
        flow: Flow<ManagerChatItem>
    ) {
        flow.collect { message ->
            reduce {
                state.copy(chatItems = state.chatItems + message.toUiItem(loadStatus = LoadStatus.Loading))
            }
            delay(1500.milliseconds)
            reduce {
                state.copy(
                    chatItems = state.chatItems.map { item ->
                        if (item.message.id == message.id) item.copy(loadStatus = LoadStatus.Idle)
                        else item
                    }
                )
            }
        }
    }

}
package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kr.co.call.domain.model.chatting.ManagerChatItem
import kr.co.call.domain.model.chatting.UserMessage
import java.time.LocalDate
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
import kr.co.call.impl.model.ManagerChatUiItem
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

    // 용량 1의 작업 큐. 가득 차면 새로 들어온 요청을 버림(DROP_LATEST)
    private val intentChannel = Channel<ManagerChatRoomIntent>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    init {
        intent {
            // 첫 매니저 인사
            appendManagerMessages(firstManagerChatUseCase())

            // 채널에 전달된 Intent를 지속적으로 소비하며 순차 처리.
            // 단일 코루틴이므로 직렬 처리 보장
            for (chatIntent in intentChannel) {
                processIntent(chatIntent)
            }
        }
    }

    // UI에 노출할 함수
    fun handleIntent(intent: ManagerChatRoomIntent) {
        // 발생한 intent를 큐에 send
        intentChannel.trySend(intent)
    }

    // Intent별 사용자 액션을 처리하고,
    // 해당 액션에 대응하는 사용자 메시지와 매니저 응답 Flow를 연결
    // 각 UseCase Flow에서 방출되는 메시지는 순차적으로 채팅 상태에 반영
    private suspend fun Syntax<ManagerChatRoomUiState, Nothing>.processIntent(
        intent: ManagerChatRoomIntent
    ) {
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

    // 사용자의 선택 메시지를 추가한 뒤, 해당 요청에 대한 매니저 응답을 처리
    private suspend fun Syntax<ManagerChatRoomUiState, Nothing>.sendUserMessageThen(
        content: String,
        flow: Flow<ManagerChatItem>
    ) {
        val userMessage = UserMessage(
            id = UUID.randomUUID().toString(),
            content = content,
            createdAt = LocalDateTime.now()
        )

        // 유저 메세지 추가.
        reduce {
            state.copy(
                chatItems = state.chatItems
                    + dateSeparatorIfNeeded(state.chatItems, userMessage.createdAt.toLocalDate())
                    + userMessage.toUiItem()
            )
        }

        // 매니저 메세지 flow 구독
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
                state.copy(
                    chatItems = state.chatItems
                        + dateSeparatorIfNeeded(state.chatItems, message.createdAt.toLocalDate())
                        + message.toUiItem(loadStatus = LoadStatus.Loading)
                )
            }

            delay(1500.milliseconds)

            reduce {
                state.copy(
                    chatItems = state.chatItems.map { item ->
                        if (item is ManagerChatUiItem.Message && item.message.id == message.id)
                            item.copy(loadStatus = LoadStatus.Idle)
                        else item
                    }
                )
            }
        }
    }

    // 마지막 메시지의 날짜와 다를 경우에만 DateSeparator를 반환
    private fun dateSeparatorIfNeeded(
        chatItems: List<ManagerChatUiItem>,
        date: LocalDate
    ): List<ManagerChatUiItem> {

        // 현재 채팅 목록에서 마지막 메시지 아이템의 날짜를 가져옴
        // DateSeparator는 제외하고 실제 메시지 데이터만 기준으로 비교
        val lastDate = chatItems
            .filterIsInstance<ManagerChatUiItem.Message>()
            .lastOrNull()
            ?.message?.createdAt?.toLocalDate()

        // 기존 메시지가 없거나 마지막 메시지와 날짜가 다르면
        // 새로운 날짜 구분선을 추가
        return if (lastDate == null || lastDate != date) {
            listOf(ManagerChatUiItem.DateSeparator(date))
        } else {
            emptyList()
        }
    }
}
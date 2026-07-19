package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kr.co.call.domain.usecase.chatting.FirstManagerChatUseCase
import kr.co.call.domain.usecase.chatting.WantToGetCallScheduleUseCase
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.mapper.UiModelMapper.toUiItem
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ManagerChatRoomViewModel @Inject constructor(
    private val firstManagerChatUseCase: FirstManagerChatUseCase,
    private val wantToGetCallScheduleUseCase: WantToGetCallScheduleUseCase
): ViewModel(), ContainerHost<ManagerChatRoomUiState, Nothing> {

    override val container: Container<ManagerChatRoomUiState, Nothing> = container(
        initialState = ManagerChatRoomUiState()
    )

    init {
        collectFirstMessages()
    }

    // 초기 매니저 메시지를 순차적으로 수집하여 채팅 목록에 추가
    // 각 메시지는 먼저 로딩 상태로 노출한 뒤, 일정 시간이 지나면 로딩을 해제하여 실제 메시지 내용을 표시
    private fun collectFirstMessages() = intent {
        firstManagerChatUseCase().collect { message ->

            // 로딩 상태의 메시지를 채팅 목록에 추가
            reduce {
                state.copy(
                    chatItems = state.chatItems + message.toUiItem(
                        loadStatus = LoadStatus.Loading
                    )
                )
            }

            // 매니저가 입력 중인 것처럼 보이도록 일정 시간 대기
            delay(1500.milliseconds)

            // 동일한 메시지의 로딩 상태를 해제하여 실제 내용을 표시
            reduce {
                state.copy(
                    chatItems = state.chatItems.map { item ->
                        // 현재 처리 중인 메시지라면 로딩 상태를 해제
                        if (item.message.id == message.id) {
                            item.copy(loadStatus = LoadStatus.Idle)
                        } else {
                            item
                        }
                    }
                )
            }
        }
    }

}
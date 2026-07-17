package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.repository.ChatRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.model.ChatListIntent
import kr.co.call.impl.model.ChatListSideEffect
import kr.co.call.impl.model.ChatListState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository
): ViewModel(), ContainerHost<ChatListState, ChatListSideEffect> {

    override val container: Container<ChatListState, ChatListSideEffect> = container(
        initialState = ChatListState()
    )

    // 초기 로드
    init {
        loadChatList()
    }

    // 초기 로딩
    private fun loadChatList()  = intent {
        reduce {
            state.copy(
                status = LoadStatus.Loading
            )
        }

        chatRepository.getChatList().fold(
            onSuccess = {
                reduce {
                    state.copy(
                        status = LoadStatus.Idle,
                        chatList = it
                    )
                }
            },
            onFailure = {

            }
        )
    }

    fun handleIntent(intent: ChatListIntent) {
        when (intent) {
            is ChatListIntent.ClickChatRoom -> emitNavigateToChatRoom(intent.roomId)
        }
    }

    private fun emitNavigateToChatRoom(roomId: Long) = intent {
        postSideEffect(ChatListSideEffect.NavigateToChatRoom(roomId))
    }




}
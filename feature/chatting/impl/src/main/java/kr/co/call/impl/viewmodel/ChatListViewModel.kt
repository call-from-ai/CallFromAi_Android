package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.repository.ChatRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.intent.ChatListIntent
import kr.co.call.impl.sideeffect.ChatListSideEffect
import kr.co.call.impl.state.ChatListState
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
            ChatListIntent.ClickManagerChatRoom -> emitNavigateToManagerChatRoom()
            is ChatListIntent.DeleteChatRoom -> deleteChatRoom(intent.roomId)
            is ChatListIntent.UpdateAlarmSetting -> updateAlarmSetting(intent.roomId)
            is ChatListIntent.ClickDeleteChatRoom -> showDeleteDialog(intent.roomId)
            ChatListIntent.DismissDeleteDialog -> dismissDeleteDialog()
        }
    }

    private fun emitNavigateToChatRoom(roomId: Long) = intent {
        postSideEffect(ChatListSideEffect.NavigateToChatRoom(roomId))
    }

    private fun emitNavigateToManagerChatRoom() = intent {
        postSideEffect(ChatListSideEffect.NavigateToManagerChatRoom)
    }

    private fun showDeleteDialog(roomId: Long) = intent {
        reduce {
            state.copy(
                showDeleteChatRoomDialog = true,
                deleteTargetRoomId = roomId,
            )
        }
    }

    private fun dismissDeleteDialog() = intent {
        reduce {
            state.copy(
                showDeleteChatRoomDialog = false
            )
        }
    }

    private fun updateAlarmSetting(roomId: Long) = intent {
        chatRepository.updateAlarmSetting(roomId).fold(
            onSuccess = {
                reduce {
                    state.copy(
                        chatList = state.chatList.map { chat ->
                            if (chat.chatRoomId == roomId) {
                                chat.copy(isAlarmEnabled = !chat.isAlarmEnabled)
                            } else {
                                chat
                            }
                        }
                    )
                }
            },
            onFailure = {
                //TODO: 추후 에러 처리
            }
        )
    }

    private fun deleteChatRoom(roomId: Long) = intent {
        chatRepository.deleteChatRoom(roomId).fold(
            onSuccess = {
                reduce {
                    state.copy(
                        chatList = state.chatList.filter { it.chatRoomId != roomId },
                        showDeleteChatRoomDialog = false,
                        deleteTargetRoomId = -1,
                    )
                }
            },
            onFailure = {
                //TODO: 추후 에러 처리
            }
        )
    }

}
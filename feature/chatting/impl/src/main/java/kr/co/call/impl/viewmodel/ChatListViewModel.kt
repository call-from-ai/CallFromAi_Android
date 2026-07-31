package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.model.chatting.ChatEvent
import kr.co.call.domain.repository.ChatEventRepository
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
    private val chatRepository: ChatRepository,
    private val chatEventRepository: ChatEventRepository,
) : ViewModel(), ContainerHost<ChatListState, ChatListSideEffect> {

    override val container: Container<ChatListState, ChatListSideEffect> = container(
        initialState = ChatListState()
    ) {
        loadChatList()
        observeChatEvents()
    }

    // 초기 로딩
    private fun loadChatList() = intent {
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
                reduce {
                    state.copy(
                        // TODO: 현재는 임시 구현, 추후 변경 가능성 높음
                        status = LoadStatus.Error("채팅 목록을 불러올 수 없습니다. 잠시 후 다시 시도해주세요")
                    )
                }
            }
        )
    }

    // ChatEventRepository 이벤트 수신 → 채팅 목록 낙관적 업데이트
    private fun observeChatEvents() = intent {
        chatEventRepository.events.collect { event ->
            when (event) {
                is ChatEvent.MessageSent -> reduce {
                    state.copy(
                        chatList = state.chatList.map { chat ->
                            if (chat.chatRoomId == event.roomId) {
                                chat.copy(
                                    content = event.content,
                                    whenSubmitted = event.whenSubmitted,
                                )
                            } else chat
                        }
                    )
                }
            }
        }
    }

    fun handleIntent(intent: ChatListIntent) {
        when (intent) {
            is ChatListIntent.ClickChatRoom -> emitNavigateToChatRoom(intent.roomId)
            ChatListIntent.ClickManagerChatRoom -> emitNavigateToManagerChatRoom()
            is ChatListIntent.DeleteChatRoom -> deleteChatRoom(intent.roomId)
            is ChatListIntent.UpdateAlarmSetting -> updateAlarmSetting(intent.roomId, intent.isMuted)
            is ChatListIntent.ClickDeleteChatRoom -> showDeleteDialog(intent.roomId)
            ChatListIntent.DismissDeleteDialog -> dismissDeleteDialog()
        }
    }

    private fun emitNavigateToChatRoom(roomId: Long) = intent {
        // 채팅방 진입 시 해당 방의 읽지 않은 메시지 수를 즉시 0으로 반영
        reduce {
            state.copy(
                chatList = state.chatList.map { chat ->
                    if (chat.chatRoomId == roomId) chat.copy(unReadMessageCount = "0")
                    else chat
                }
            )
        }
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

    private fun updateAlarmSetting(roomId: Long, isMuted: Boolean) = intent {
        chatRepository.updateAlarmSetting(roomId, isMuted).fold(
            onSuccess = {
                reduce {
                    state.copy(
                        chatList = state.chatList.map { chat ->
                            if (chat.chatRoomId == roomId) {
                                chat.copy(isMuted = isMuted)
                            } else {
                                chat
                            }
                        }
                    )
                }
                postSideEffect(ChatListSideEffect.ShowToast("알람 설정이 변경되었습니다."))
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
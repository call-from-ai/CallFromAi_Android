package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kr.co.call.domain.model.chatting.ChatSseEvent
import kr.co.call.domain.repository.ChatRepository
import kr.co.call.domain.repository.ChatSseRepository
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
    private val chatSseRepository: ChatSseRepository
) : ViewModel(), ContainerHost<ChatListState, ChatListSideEffect> {

    override val container: Container<ChatListState, ChatListSideEffect> = container(
        initialState = ChatListState()
    ) {
        loadChatList()
        observeSseEvents()
    }

    private fun observeSseEvents() = intent {
        chatSseRepository.sseFlow
            .filterIsInstance<ChatSseEvent.Message>()
            .collect { refreshChatList() }
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

    // onResume 시 로딩 없이 조용히 목록 갱신 — 변경된 항목만 교체
    private fun refreshChatList() = intent {
        chatRepository.getChatList()
            .onSuccess { newList ->
                // 기존 목록을 채팅방 ID 기준으로 매핑
                val currentMap = state.chatList.associateBy { it.chatRoomId }

                // 변경되지 않은 항목은 기존 객체를 유지하고, 변경된 항목만 새 데이터로 교체
                val merged = newList.map { new -> currentMap[new.chatRoomId]?.takeIf { it == new } ?: new }

                // 실제 변경이 있는 경우에만 상태를 갱신
                if (merged != state.chatList) {
                    reduce { state.copy(chatList = merged) }
                }
            }
        // 실패는 무시 — 백그라운드 갱신이므로 사용자에게 노출하지 않음
    }

    fun handleIntent(intent: ChatListIntent) {
        when (intent) {
            is ChatListIntent.ClickChatRoom -> emitNavigateToChatRoom(intent.roomId)
            ChatListIntent.ClickManagerChatRoom -> emitNavigateToManagerChatRoom()
            is ChatListIntent.DeleteChatRoom -> deleteChatRoom(intent.roomId)
            is ChatListIntent.UpdateAlarmSetting -> updateAlarmSetting(intent.roomId, intent.isMuted)
            is ChatListIntent.ClickDeleteChatRoom -> showDeleteDialog(intent.roomId)
            ChatListIntent.DismissDeleteDialog -> dismissDeleteDialog()
            ChatListIntent.OnResume -> refreshChatList()
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
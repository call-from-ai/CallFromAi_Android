package kr.co.call.impl.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kr.co.call.api.ChatRoomNavKey
import kr.co.call.domain.model.chatting.ChatSseEvent
import kr.co.call.domain.repository.ChatRepository
import kr.co.call.domain.repository.ChatSseRepository
import kr.co.call.impl.intent.ChatRoomIntent
import kr.co.call.impl.mapper.UiModelMapper.sseLoadingBubble
import kr.co.call.impl.mapper.UiModelMapper.toUiItem
import kr.co.call.impl.model.ChatItemUiModel
import kr.co.call.impl.sideeffect.ChatRoomSideEffect
import kr.co.call.impl.state.ChatRoomUiState
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.util.buildOptimisticMessage
import kr.co.call.impl.util.insertDateSeparators
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
@HiltViewModel(assistedFactory = ChatRoomViewModel.Factory::class)
class ChatRoomViewModel @AssistedInject constructor(
    private val chatRepository: ChatRepository,
    private val chatSseRepository: ChatSseRepository,
    @Assisted val navKey: ChatRoomNavKey,
): ViewModel(), ContainerHost<ChatRoomUiState, ChatRoomSideEffect> {

    @AssistedFactory
    interface Factory {
        fun create(navKey: ChatRoomNavKey): ChatRoomViewModel
    }

    override val container: Container<ChatRoomUiState, ChatRoomSideEffect> = container(
        initialState = ChatRoomUiState()
    ) {
        loadHeader()
        readChats(navKey.roomId)
        observeSseEvents()
    }

    // 채팅 메시지 목록을 PagingData로 불러오고, 날짜 구분선을 삽입한 뒤 UI 모델로 변환
    val chats = chatRepository.getChats(navKey.roomId)
        .map { pagingData ->
            pagingData
                .insertDateSeparators()
                .map { item ->
                    item.toUiItem()
                }
        }
        .cachedIn(viewModelScope)

    // 채팅방 헤더 정보를 조회하고 UI 상태에 반영
    private fun loadHeader() = intent {
        chatRepository.getChatRoomHeader(navKey.roomId)
            .onSuccess { header ->
                reduce { state.copy(topHeader = header.toUiItem()) }
            }
            .onFailure {
                postSideEffect(ChatRoomSideEffect.ShowToast("정보를 불러오지 못했습니다. 다시 시도해주세요"))
            }
    }

    // 메세지 읽음 처리
    // 실패처리는 하지 않음. 읽음 api에 실패처리를 하는 것이 오히려 부자연스러울 수 있다는 판단.
    private fun readChats(roomId: Long) = intent {
        chatRepository.readChats(roomId)
    }

    // SSE Loading 이벤트 수신 시 현재 채팅방 ID에 해당하는 로딩 버블을 추가하고,
    // Message 이벤트 수신 시 해당 로딩 버블을 실제 메시지로 교체
    private fun observeSseEvents() = intent {
        chatSseRepository.sseFlow
            .filter { event ->
                when (event) {
                    is ChatSseEvent.Loading -> event.chatRoomId == navKey.roomId
                    is ChatSseEvent.Message -> event.message.chatRoomId == navKey.roomId
                    else -> false
                }
            }
            .collect { event ->
                when (event) {
                    is ChatSseEvent.Loading -> {
                        reduce {
                            state.copy(chatItems = listOf(sseLoadingBubble(SSE_LOADING_CLIENT_ID)) + state.chatItems)
                        }
                    }
                    is ChatSseEvent.Message -> {
                        // 읽음 처리
                        chatRepository.readChats(navKey.roomId)

                        // 받은 메세지를 UI모델로 변환
                        val receivedItem = event.message.toUiItem()


                        reduce {
                            // 로딩 버블을 제거하고 수신 메시지를 항상 index 0(최하단)에 추가
                            // 제자리 교체 시 msg2 전송 후 reply가 오면 msg1~msg2 사이에 끼는 문제 방지
                            val withoutBubble = state.chatItems.filter {
                                it !is ChatItemUiModel.Message || it.clientId != SSE_LOADING_CLIENT_ID
                            }
                            state.copy(chatItems = listOf(receivedItem) + withoutBubble)
                        }
                    }
                    else -> Unit
                }
            }
    }

    // UI에 노출할 함수
    fun handleIntent(intent: ChatRoomIntent) {
        when (intent) {
            is ChatRoomIntent.ClickCall -> showCallDialog()
            ChatRoomIntent.ClickCamera -> emitGoToCamera()
            ChatRoomIntent.ClickGallery -> emitGoToGallery()
            is ChatRoomIntent.DeleteMessage -> deleteMessage(navKey.roomId, intent.messageId)
            is ChatRoomIntent.SendMessage -> sendMessage(intent)
            is ChatRoomIntent.LongPressMessage -> selectMessage(intent.messageId)
            is ChatRoomIntent.GoToCall -> emitNavigateToCall(intent.characterId)
            is ChatRoomIntent.ImagesPicked -> uploadImages(intent.uri)
            is ChatRoomIntent.PictureTaken -> uploadImages(intent.uri)
            ChatRoomIntent.CancelImage -> clearSelectedImage()
            ChatRoomIntent.DismissDeleteDialog -> dismissCallDialog()
            is ChatRoomIntent.ClickProfile -> showProfile(intent.imageUrl)
            ChatRoomIntent.DismissProfile -> dismissProfile()
            ChatRoomIntent.DismissPopup -> dismissPopup()
            ChatRoomIntent.ShowNotMainDialog -> showNotMainDialog()
            ChatRoomIntent.DismissNotMainDialog -> dismissNotMainDialog()
        }
    }

    // 메시지를 UI에 먼저 반영한 뒤 서버로 전송하는 메시지 처리
    private fun sendMessage(intent: ChatRoomIntent.SendMessage) = intent {
        // 서버 전송 전 UI에 메시지를 먼저 표시하는 낙관적 업데이트 처리
        val optimisticMsg = buildOptimisticMessage(
            message = intent.message,
            image = intent.image,
            imageUri = intent.imageUri,
        )

        // 임시 메시지 추가 및 선택 이미지 초기화 (텍스트는 로컬에서 관리)
        reduce {
            state.copy(
                chatItems = listOf(optimisticMsg) + state.chatItems,
                textFieldState = state.textFieldState.copy(selectedImage = null),
            )
        }

        // 실제 메시지 서버 전송
        chatRepository.sendMessage(
            roomId = navKey.roomId,
            message = intent.message,
            image = intent.image,
        ).onSuccess { serverMessage ->
            // 전송 성공 시 서버에서 받은 chatMessageId, senderType, messageType을 낙관적 메시지에 반영
            reduce {
                state.copy(
                    chatItems = state.chatItems.map { item ->
                        if (item is ChatItemUiModel.Message
                            && item.clientId == optimisticMsg.clientId
                            ) {
                            item.copy(
                                chatMessageId = serverMessage.chatMessageId,
                                senderType = serverMessage.senderType,
                                messageType = serverMessage.messageType,
                                loadStatus = LoadStatus.Idle,
                            )
                        } else item
                    }
                )
            }

        }.onFailure {
            // 전송 실패 시 낙관적으로 추가했던 임시 메시지 제거
            reduce {
                state.copy(
                    chatItems = state.chatItems.filterNot {
                        it is ChatItemUiModel.Message && it.clientId == optimisticMsg.clientId
                    }
                )
            }

            postSideEffect(ChatRoomSideEffect.ShowToast("메세지를 전송할 수 없습니다. 잠시 후 다시 시도해주세요"))
        }
    }

    // isMain이면 통화 확인 다이얼로그, 아니면 메인 캐릭터 아님 다이얼로그 표시
    private fun showCallDialog() = intent {
        if (state.topHeader.isMain) {
            reduce { state.copy(showCallDialog = true) }
        } else {
            reduce { state.copy(showNotMainDialog = true) }
        }
    }

    // 통화 연결 확인 다이얼로그 닫기
    private fun dismissCallDialog() = intent {
        reduce {
            state.copy(
                showCallDialog = false
            )
        }
    }

    // 메인 캐릭터가 아닙니다 다이얼로그 표시 상태 변경
    private fun showNotMainDialog() = intent {
        reduce { state.copy(showNotMainDialog = true) }
    }

    // 메인 캐릭터가 아닙니다 다이얼로그 닫기
    private fun dismissNotMainDialog() = intent {
        reduce {
            state.copy(
                showNotMainDialog = false
            )
        }
    }

    // 선택한 이미지를 입력창에 추가
    private fun uploadImages(uri: Uri?) = intent {
        if (uri == null) return@intent
        reduce {
            state.copy(
                textFieldState = state.textFieldState.copy(selectedImage = uri)
            )
        }
    }

    // 입력창에 선택된 이미지 제거
    private fun clearSelectedImage() = intent {
        reduce {
            state.copy(
                textFieldState = state.textFieldState.copy(selectedImage = null)
            )
        }
    }

    // 롱프레스된 메시지를 선택 상태로 변경
    private fun selectMessage(messageId: Long) = intent {
        reduce {
            state.copy(
                selectedMessageId = messageId
            )
        }
    }

    // 선택한 메시지를 삭제하고 삭제 상태 반영
    private fun deleteMessage(
        chatroomId: Long,
        messageId: Long
    ) = intent {
        chatRepository.deleteMessage(chatroomId, messageId).fold(
            onSuccess = {
                reduce {
                    state.copy(
                        deletedIds = state.deletedIds + messageId,
                        selectedMessageId = null
                    )
                }
                postSideEffect(ChatRoomSideEffect.ShowToast("메세지가 삭제되었습니다."))
            },
            onFailure = {
                postSideEffect(ChatRoomSideEffect.ShowToast("메시지를 삭제할 수 없습니다. 잠시 후 다시 시도해주세요"))
            }
        )
    }

    // 메시지 액션 팝업 닫기
    private fun dismissPopup() = intent {
        reduce {
            state.copy(
                selectedMessageId = null
            )
        }
    }

    // 프로필 이미지 확대 표시
    private fun showProfile(url: String) = intent {
        reduce { state.copy(expandedProfileUrl = url) }
    }

    // 확대된 프로필 이미지 닫기
    private fun dismissProfile() = intent {
        reduce { state.copy(expandedProfileUrl = null) }
    }

    // 통화 화면 이동 이벤트 처리
    private fun emitNavigateToCall(characterId: Long) = intent {
        //TODO: 통화 화면으로 이동
    }

    // 카메라 실행 이벤트 전달
    private fun emitGoToCamera() = intent {
        postSideEffect(ChatRoomSideEffect.GoToCamera)
    }

    // 갤러리 실행 이벤트 전달
    private fun emitGoToGallery() = intent {
        postSideEffect(ChatRoomSideEffect.GoToGallery)
    }

    private companion object {
        const val SSE_LOADING_CLIENT_ID = "sse_loading"
    }
}

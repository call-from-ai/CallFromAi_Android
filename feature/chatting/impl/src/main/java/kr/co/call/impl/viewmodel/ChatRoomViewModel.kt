package kr.co.call.impl.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kr.co.call.api.ChatRoomNavKey
import kr.co.call.domain.model.chatting.ChatItem
import kr.co.call.domain.repository.ChatRepository
import kr.co.call.impl.intent.ChatRoomIntent
import kr.co.call.impl.mapper.UiModelMapper.toUiItem
import kr.co.call.impl.model.ChatItemUiModel
import kr.co.call.impl.model.TextFieldState
import kr.co.call.impl.sideeffect.ChatRoomSideEffect
import kr.co.call.impl.state.ChatRoomUiState
import kr.co.call.impl.util.buildOptimisticMessage
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
@HiltViewModel(assistedFactory = ChatRoomViewModel.Factory::class)
class ChatRoomViewModel @AssistedInject constructor(
    private val chatRepository: ChatRepository,
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
    }

    // 채팅 메시지 목록을 PagingData로 불러오고, 삭제된 메시지를 제외한 뒤 UI 모델로 변환
    // _deletedIds가 변경될 때마다 flatMapLatest로 새 페이징 흐름을 생성해 필터 적용
    val chats = chatRepository.getChats(navKey.roomId)
        .map { pagingData ->
            pagingData.map { item ->
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
    }

    // UI에 노출할 함수
    fun handleIntent(intent: ChatRoomIntent) {
        when (intent) {
            is ChatRoomIntent.ClickCall -> showCallDialog()
            ChatRoomIntent.ClickCamera -> emitGoToCamera()
            ChatRoomIntent.ClickGallery -> emitGoToGallery()
            is ChatRoomIntent.DeleteMessage -> deleteMessage(intent.messageId)
            is ChatRoomIntent.SendMessage -> sendMessage(intent)
            is ChatRoomIntent.LongPressMessage -> selectMessage(intent.messageId)
            is ChatRoomIntent.GoToCall -> emitNavigateToCall(intent.characterId)
            is ChatRoomIntent.ImagesPicked -> uploadImages(intent.uri)
            is ChatRoomIntent.PictureTaken -> uploadImages(intent.uri)
            ChatRoomIntent.CancelImage -> clearSelectedImage()
            ChatRoomIntent.DismissDeleteDialog -> dismissDeleteDialog()
            is ChatRoomIntent.ClickProfile -> showProfile(intent.imageUrl)
            ChatRoomIntent.DismissProfile -> dismissProfile()
            ChatRoomIntent.DismissPopup -> dismissPopup()
        }
    }

    // 입력창 텍스트 변경 사항을 UI 상태에 반영
    fun onTextChange(text: String) = intent {
        reduce { state.copy(textFieldState = state.textFieldState.copy(text = text)) }
    }

    // 메시지를 UI에 먼저 반영한 뒤 서버로 전송하는 메시지 처리
    private fun sendMessage(intent: ChatRoomIntent.SendMessage) = intent {
        // 서버 전송 전 UI에 메시지를 먼저 표시하는 낙관적 업데이트 처리
        val optimisticMsg = buildOptimisticMessage(
            message = intent.message,
            image = intent.image,
            imageUri = intent.imageUri,
        )

        // 입력창 초기화 및 임시 메시지 추가
        reduce {
            state.copy(
                chatItems = listOf(optimisticMsg) + state.chatItems,
                textFieldState = TextFieldState(),
            )
        }

        // 실제 메시지 서버 전송
        chatRepository.sendMessage(
            roomId = navKey.roomId,
            message = intent.message,
            image = intent.image,
        ).onFailure {
            // 전송 실패 시 낙관적으로 추가했던 임시 메시지 제거
            // TODO: 임시 구현. 요구사항에 따라 달라질 수 있음
            reduce {
                state.copy(
                    chatItems = state.chatItems.filterNot {
                        it is ChatItemUiModel.Message && it.clientId == optimisticMsg.clientId
                    }
                )
            }
        }
    }

    // 통화 연결 확인 다이얼로그 표시 상태 변경
    private fun showCallDialog() = intent {
        reduce {
            state.copy(
                showCallDialog = true
            )
        }
    }

    // 통화 연결 확인 다이얼로그 닫기
    private fun dismissDeleteDialog() = intent {
        reduce {
            state.copy(
                showCallDialog = false
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
    private fun deleteMessage(messageId: Long) = intent {
        chatRepository.deleteMessage(messageId).fold(
            onSuccess = {
                reduce {
                    state.copy(
                        deletedIds = state.deletedIds + messageId,
                        selectedMessageId = null
                    )
                }
            },
            onFailure = {
                // TODO error
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

}

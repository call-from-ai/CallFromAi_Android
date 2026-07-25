package kr.co.call.impl.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kr.co.call.api.ChatRoomNavKey
import kr.co.call.domain.repository.ChatRepository
import kr.co.call.impl.intent.ChatRoomIntent
import kr.co.call.impl.mapper.UiModelMapper.toUiItem
import kr.co.call.impl.sideeffect.ChatRoomSideEffect
import kr.co.call.impl.state.ChatRoomUiState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

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

    init {
        Timber.tag("ChatRoomVM")
            .d("create room=%d vm=%d", navKey.roomId, hashCode())
    }

    val chats = chatRepository
        .getChats(navKey.roomId)
        .map { pagingData -> pagingData.map { it.toUiItem() } }
        .cachedIn(viewModelScope)

    private fun loadHeader() = intent {
        chatRepository.getChatRoomHeader(navKey.roomId)
            .onSuccess { header ->
                reduce { state.copy(topHeader = header.toUiItem()) }
            }
    }

    fun handleIntent(intent: ChatRoomIntent) {
        when (intent) {
            is ChatRoomIntent.ClickCall -> showCallDialog(intent.characterId)
            ChatRoomIntent.ClickCamera -> emitGoToCamera()
            ChatRoomIntent.ClickGallery -> emitGoToGallery()
            is ChatRoomIntent.DeleteMessage -> TODO()
            is ChatRoomIntent.LongPressMessage -> TODO()
            is ChatRoomIntent.SendMessage -> sendMessage(intent)
            is ChatRoomIntent.GoToCall -> emitNavigateToCall(intent.characterId)
            is ChatRoomIntent.ImagesPicked -> uploadImages(intent.uri)
            is ChatRoomIntent.PictureTaken -> uploadImages(intent.uri)
            ChatRoomIntent.CancelImage -> clearSelectedImage()
            ChatRoomIntent.DismissDeleteDialog -> dismissDeleteDialog()
            is ChatRoomIntent.ClickProfile -> showProfile(intent.imageUrl)
            ChatRoomIntent.DismissProfile -> dismissProfile()
        }
    }

    fun onTextChange(text: String) = intent {
        reduce { state.copy(textFieldState = state.textFieldState.copy(text = text)) }
    }

    private fun sendMessage(intent: ChatRoomIntent.SendMessage) = intent {
        // navKey.roomId 사용, intent.roomId는 무시
        chatRepository.sendMessage(
            roomId = navKey.roomId,
            message = intent.message,
            image = intent.image,
        )
    }



    private fun showCallDialog(characterId: Long) = intent {
        reduce {
            state.copy(
                showDeleteChatRoomDialog = true
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


    private fun uploadImages(uri: Uri?) = intent {
        if (uri == null) return@intent
        reduce {
            state.copy(
                textFieldState = state.textFieldState.copy(selectedImage = uri)
            )
        }
    }

    private fun clearSelectedImage() = intent {
        reduce {
            state.copy(
                textFieldState = state.textFieldState.copy(selectedImage = null)
            )
        }
    }

    private fun showProfile(url: String) = intent {
        reduce { state.copy(expandedProfileUrl = url) }
    }
    private fun dismissProfile() = intent {
        reduce { state.copy(expandedProfileUrl = "") }
    }

    private fun emitNavigateToCall(characterId: Long) = intent {
        //TODO: 통화 화면으로 이동
    }

    private fun emitGoToCamera() = intent {
        postSideEffect(ChatRoomSideEffect.GoToCamera)
    }

    private fun emitGoToGallery() = intent {
        postSideEffect(ChatRoomSideEffect.GoToGallery)
    }



}

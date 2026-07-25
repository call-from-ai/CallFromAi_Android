package kr.co.call.impl.viewmodel

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

    fun deleteMessage(chatMessageId: Long) = intent {
        reduce {
            state.copy(deletedIds = state.deletedIds + chatMessageId)
        }
    }

}

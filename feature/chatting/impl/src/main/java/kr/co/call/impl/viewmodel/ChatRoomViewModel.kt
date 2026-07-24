package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kr.co.call.domain.repository.ChatRepository
import kr.co.call.impl.mapper.UiModelMapper.toUiItem
import kr.co.call.impl.sideeffect.ChatRoomSideEffect
import kr.co.call.impl.state.ChatRoomUiState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel(assistedFactory = ChatRoomViewModel.Factory::class)
class ChatRoomViewModel @AssistedInject constructor(
    private val chatRepository: ChatRepository,
    @Assisted private val roomId: Long,
): ViewModel(), ContainerHost<ChatRoomUiState, ChatRoomSideEffect> {

    @AssistedFactory
    interface Factory {
        fun create(roomId: Long): ChatRoomViewModel
    }

    override val container: Container<ChatRoomUiState, ChatRoomSideEffect> = container(
        initialState = ChatRoomUiState()
    )

    val chats = chatRepository
        .getChats(roomId)
        .map { pagingData -> pagingData.map { it.toUiItem() } }
        .cachedIn(viewModelScope)

    fun deleteMessage(chatMessageId: Long) = intent {
        reduce {
            state.copy(deletedIds = state.deletedIds + chatMessageId)
        }
    }

}

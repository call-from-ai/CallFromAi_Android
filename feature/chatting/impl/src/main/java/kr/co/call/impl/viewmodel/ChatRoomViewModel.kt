package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import kr.co.call.domain.repository.ChatRepository
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository
): ViewModel(), ContainerHost<ChatRoomUiState, ChatRoomSideEffect> {
    override val container: Container<ChatRoomUiState, ChatRoomSideEffect> = container(
        initialState = TODO()
    )


}
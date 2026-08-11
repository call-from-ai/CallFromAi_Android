package kr.co.call.impl.viewmodel

import kr.co.call.domain.model.mypage.AiCharacter
import kr.co.call.domain.util.LoadStatus

sealed interface ChatHistoryUi {
    data object Hidden : ChatHistoryUi
    data object Loading : ChatHistoryUi
    data class Ready(val summary: String) : ChatHistoryUi
}

data class CharacterManagementState(
    val aiCharacters: List<AiCharacter> = emptyList(),
    val loadStatus: LoadStatus = LoadStatus.Idle,
    val chatHistoryUi: ChatHistoryUi = ChatHistoryUi.Hidden,
)
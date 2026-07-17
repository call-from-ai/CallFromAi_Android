package kr.co.call.impl.viewmodel

import kr.co.call.domain.model.mypage.AiCharacter

sealed interface CharacterManagementSideEffect {
    data class ShowChatHistorySummary(val aiCharacter: AiCharacter) : CharacterManagementSideEffect
    data class ShowDeleteConfirmDialog(val aiCharacter: AiCharacter) : CharacterManagementSideEffect
    data object ShowMainCharacterDeleteBlocked : CharacterManagementSideEffect
    data object ShowAddCharacterBlocked : CharacterManagementSideEffect
    data object NavigateToAddCharacter : CharacterManagementSideEffect
}
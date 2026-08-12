package kr.co.call.impl.viewmodel

import kr.co.call.domain.model.mypage.AiCharacter

sealed interface CharacterManagementSideEffect {
    data class ShowDeleteConfirmDialog(val aiCharacter: AiCharacter) : CharacterManagementSideEffect
    data object ShowMainCharacterDeleteBlocked : CharacterManagementSideEffect
    data object ShowAddCharacterBlocked : CharacterManagementSideEffect
    data object NavigateToAddCharacter : CharacterManagementSideEffect
    data class NavigateToEditCharacter(val characterId: Long) : CharacterManagementSideEffect
    data class ShowMessage(val message: String) : CharacterManagementSideEffect
}
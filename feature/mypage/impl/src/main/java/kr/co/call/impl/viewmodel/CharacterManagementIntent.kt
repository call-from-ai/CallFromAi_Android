package kr.co.call.impl.viewmodel

import kr.co.call.domain.model.mypage.AiCharacter

sealed interface CharacterManagementIntent {
    data class ClickChatHistory(val aiCharacter: AiCharacter) : CharacterManagementIntent
    data class ClickDeleteCharacter(val aiCharacter: AiCharacter) : CharacterManagementIntent
    data class ConfirmDeleteCharacter(val aiCharacter: AiCharacter) : CharacterManagementIntent
    data object ClickAddCharacter : CharacterManagementIntent
}
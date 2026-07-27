package kr.co.call.impl.viewmodel

import kr.co.call.domain.model.mypage.AiCharacter
import kr.co.call.domain.util.LoadStatus

data class CharacterManagementState(
    val aiCharacters: List<AiCharacter> = emptyList(),
    val loadStatus: LoadStatus = LoadStatus.Idle,
)
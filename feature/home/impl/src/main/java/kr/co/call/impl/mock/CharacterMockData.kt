package kr.co.call.impl.mock

import kr.co.call.impl.viewmodel.model.CharacterOptionUiModel

object CharacterMockData {
    val uiModels: List<CharacterOptionUiModel> = listOf(
        CharacterOptionUiModel(characterId = 1L, name = "민준", day = 30, isSelected = true),
        CharacterOptionUiModel(characterId = 2L, name = "동휘", day = 12, isSelected = false),
    )
}
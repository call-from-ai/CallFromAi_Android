package kr.co.call.impl.viewmodel.model

data class CharacterOptionUiModel(
    val characterId: Long,
    val name: String,
    val day: Int,
    val imageUrl: String? = null,
    val isSelected: Boolean = false,
)

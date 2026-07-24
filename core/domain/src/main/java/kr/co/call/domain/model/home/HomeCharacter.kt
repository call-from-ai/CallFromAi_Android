package kr.co.call.domain.model.home

data class HomeCharacter(
    val id: Long,
    val name: String,
    val relationshipDays: Int,
    val imageUrl: String?,
    val isMain: Boolean,
)

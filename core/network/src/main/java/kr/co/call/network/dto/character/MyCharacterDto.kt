package kr.co.call.network.dto.character

data class MyCharacterDto(
    val characterId: Long,
    val name: String,
    val main: Boolean,
    val createdAt: String,
    val startedAt: String,
    val imageUrl: String,
    val daysTogether: Int,
    val lastMessageAt: String,
)

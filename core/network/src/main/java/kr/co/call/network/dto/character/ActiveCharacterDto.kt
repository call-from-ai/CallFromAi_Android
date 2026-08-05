package kr.co.call.network.dto.character

data class ActiveCharacterDto(
    val characterId: Long,
    val name: String? = null,
    val preferTime: String? = null,
    val main: Boolean = false,
)

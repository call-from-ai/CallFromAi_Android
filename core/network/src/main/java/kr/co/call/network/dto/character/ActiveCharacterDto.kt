package kr.co.call.network.dto.character

data class ActiveCharacterDto(
    val characterId: Long,
    val name: String? = null,
    val gender: String? = null,
    val age: Int? = null,
    val job: String? = null,
    val imageUrl: String? = null,
    val spiceLevel: Int? = null,
    val preferTime: String? = null,
    val mbti: String? = null,
    val speechStyle: String? = null,
    val relationshipStage: String? = null,
    val main: Boolean = false,
    val traits: List<CharacterTraitDto> = emptyList(),
)

data class CharacterTraitDto(
    val code: String? = null,
    val name: String? = null,
    val priority: Int? = null,
)

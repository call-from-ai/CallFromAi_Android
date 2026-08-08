package kr.co.call.network.dto.onboarding

data class CreateCharacterRequestDto(
    val lastName: String,
    val firstName: String,
    val gender: String,
    val age: Int,
    val job: String,
    val imageUrl: String? = null,
    val spiceLevel: Int,
    val preferTime: String,
    val mbti: String? = null,
    val speechStyle: String,
    val relationshipStage: String,
    val traits: List<CharacterTraitRequestDto>,
)

data class CharacterTraitRequestDto(
    val trait: String,
    val priority: Int,
)
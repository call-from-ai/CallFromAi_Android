package kr.co.call.network.dto.character

import kr.co.call.network.dto.onboarding.CharacterTraitRequestDto

data class UpdateCharacterRequestDto(
    val lastName: String,
    val firstName: String,
    val gender: String,
    val age: Int,
    val job: String,
    val imageUrl: String,
    val spiceLevel: Int,
    val preferTime: String,
    val mbti: String,
    val speechStyle: String,
    val relationshipStage: String,
    val traits: List<CharacterTraitRequestDto>,
)

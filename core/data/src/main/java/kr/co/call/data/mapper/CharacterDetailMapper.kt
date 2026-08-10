package kr.co.call.data.mapper

import kr.co.call.domain.model.NameLimits
import kr.co.call.domain.model.mypage.CharacterDetail
import kr.co.call.domain.model.mypage.CharacterTraitDetail
import kr.co.call.domain.model.mypage.CharacterUpdateInput
import kr.co.call.network.dto.character.ActiveCharacterDto
import kr.co.call.network.dto.character.UpdateCharacterRequestDto
import kr.co.call.network.dto.onboarding.CharacterTraitRequestDto

internal fun ActiveCharacterDto.toCharacterDetail(): CharacterDetail {
    val fullName = name.orEmpty()
    val (last, first) = splitDisplayName(fullName)
    return CharacterDetail(
        characterId = characterId,
        lastName = last,
        firstName = first,
        displayName = fullName,
        gender = gender ?: "MALE",
        age = age,
        job = job,
        imageUrl = imageUrl.orEmpty(),
        spiceLevel = spiceLevel ?: 50,
        preferTime = preferTime ?: "ANYTIME",
        mbti = mbti,
        speechStyle = speechStyle ?: "CASUAL",
        relationshipStage = relationshipStage ?: "SOME",
        isMain = main,
        traits = traits
            .mapNotNull { t ->
                val code = t.code?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                CharacterTraitDetail(
                    code = code,
                    priority = t.priority ?: 0,
                )
            }
            .sortedBy { it.priority },
        isFullDetail = true,
    )
}

internal fun CharacterUpdateInput.toRequestDto(): UpdateCharacterRequestDto =
    UpdateCharacterRequestDto(
        lastName = lastName.trim().take(NameLimits.LAST_NAME_MAX),
        firstName = firstName.trim().take(NameLimits.FIRST_NAME_MAX),
        gender = gender,
        age = age,
        job = job,
        imageUrl = imageUrl,
        spiceLevel = spiceLevel,
        preferTime = preferTime,
        mbti = mbti,
        speechStyle = speechStyle,
        relationshipStage = relationshipStage,
        traits = traits.map {
            CharacterTraitRequestDto(
                trait = it.code,
                priority = it.priority,
            )
        },
    )

private fun splitDisplayName(name: String): Pair<String, String> {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return "" to ""
    if (trimmed.length == 1) return trimmed to ""
    return trimmed.take(1) to trimmed.drop(1)
}

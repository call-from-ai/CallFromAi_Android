package kr.co.call.data.mapper

import kr.co.call.domain.model.onboarding.CharacterOnboardingInput
import kr.co.call.domain.model.onboarding.CharacterTraitInput
import kr.co.call.domain.model.onboarding.CreatedCharacter
import kr.co.call.domain.model.onboarding.MemberOnboardingInput
import kr.co.call.domain.model.onboarding.PresetImage
import kr.co.call.network.dto.onboarding.CharacterTraitRequestDto
import kr.co.call.network.dto.onboarding.CreateCharacterRequestDto
import kr.co.call.network.dto.onboarding.CreateCharacterResponseDto
import kr.co.call.network.dto.onboarding.PresetImageResponseDto
import kr.co.call.network.dto.onboarding.UpdateMemberRequestDto

internal fun MemberOnboardingInput.toRequestDto(): UpdateMemberRequestDto =
    UpdateMemberRequestDto(
        lastName = lastName,
        firstName = firstName,
        imageUrl = imageUrl,
        gender = gender,
        birth = birth,
        mbti = mbti,
        job = job,
    )

internal fun CharacterOnboardingInput.toRequestDto(): CreateCharacterRequestDto =
    CreateCharacterRequestDto(
        lastName = lastName.trim().take(2),
        firstName = firstName.trim().take(5),
        gender = gender,
        age = age,
        job = job.toCharacterJobApiCode(),
        // BE : 빈 문자열은 400, null만 허용
        imageUrl = imageUrl.trim().takeIf { it.isNotEmpty() },
        spiceLevel = spiceLevel,
        preferTime = preferTime,
        mbti = mbti.trim().takeIf { it.isNotEmpty() },
        speechStyle = speechStyle,
        relationshipStage = relationshipStage,
        traits = traits.map { it.toRequestDto() },
    )

/** 레거시 code -> BE Job enum */
private fun String.toCharacterJobApiCode(): String =
    when (trim().uppercase()) {
        "STUDENT", "UNIVERSITY_STUDENT" -> "UNIVERSITY_STUDENT"
        "EMPLOYED", "EMPLOYEE" -> "EMPLOYEE"
        "UMEMPLOYED", "OTHER" -> "OTHER"
        else -> trim().uppercase()
    }

private fun CharacterTraitInput.toRequestDto(): CharacterTraitRequestDto =
    CharacterTraitRequestDto(
        trait = trait,
        priority = priority,
    )

internal fun CreateCharacterResponseDto.toDomain(): CreatedCharacter =
    CreatedCharacter(
        id = id,
        name = name,
        imageUrl = characterImageUrl,
    )

internal fun PresetImageResponseDto.toDomain(): PresetImage =
    PresetImage(
        id = presetImageId,
        imageUrl = imageUrl,
    )
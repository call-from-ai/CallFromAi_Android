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
        lastName = lastName,
        firstName = firstName,
        gender = gender,
        age = age,
        job = job,
        imageUrl = imageUrl,
        spiceLevel = spiceLevel,
        preferTime = preferTime,
        mbti = mbti,
        speechStyle = speechStyle,
        relationshipStage = relationshipStage,
        traits = traits.map { it.toRequestDto() },
    )

private fun CharacterTraitInput.toRequestDto(): CharacterTraitRequestDto =
    CharacterTraitRequestDto(
        trait = trait,
        priority = priority,
    )

internal fun CreateCharacterResponseDto.toDomain(): CreatedCharacter =
    CreatedCharacter(
        id = id,
        name = name,
    )

internal fun PresetImageResponseDto.toDomain(): PresetImage =
    PresetImage(
        id = presetImageId,
        imageUrl = imageUrl,
    )
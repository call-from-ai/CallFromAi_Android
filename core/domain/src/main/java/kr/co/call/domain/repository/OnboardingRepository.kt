package kr.co.call.domain.repository

import kr.co.call.domain.model.onboarding.CharacterOnboardingInput
import kr.co.call.domain.model.onboarding.CreatedCharacter
import kr.co.call.domain.model.onboarding.MemberOnboardingInput
import kr.co.call.domain.model.onboarding.PresetImage

interface OnboardingRepository {
    suspend fun submitMemberOnboarding(
        submission: MemberOnboardingInput,
    ):Result<Unit>
    suspend fun submitCharacterOnboarding(
        submission: CharacterOnboardingInput
    ):Result<CreatedCharacter>

    suspend fun getPresetImages(
        gender: String,
    ):Result<List<PresetImage>>
}
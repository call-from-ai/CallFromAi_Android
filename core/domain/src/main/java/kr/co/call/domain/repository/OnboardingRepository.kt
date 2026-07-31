package kr.co.call.domain.repository

import kr.co.call.domain.model.onboarding.CharacterOnboardingInput
import kr.co.call.domain.model.onboarding.MemberOnboardingInput

interface OnboardingRepository {
    suspend fun submitMemberOnboarding(
        submission: MemberOnboardingInput,
    ):Result<Unit>
    suspend fun submitCharacterOnboarding(
        submission: CharacterOnboardingInput
    ):Result<Unit>
}
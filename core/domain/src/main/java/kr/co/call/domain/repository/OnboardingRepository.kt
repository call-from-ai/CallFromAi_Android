package kr.co.call.domain.repository

import kr.co.call.domain.model.onboarding.OnboardingInput

interface OnboardingRepository {
    suspend fun submitOnboarding(
        submission: OnboardingInput,
    ):Result<Unit>
}
package kr.co.call.domain.repository

import kr.co.call.domain.model.onboarding.OnboardingSubmission

interface OnboardingRepository {
    suspend fun submitOnboarding(
        submission: OnboardingSubmission,
    ):Result<Unit>
}
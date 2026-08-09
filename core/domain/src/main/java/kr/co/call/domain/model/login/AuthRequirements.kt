package kr.co.call.domain.model.login

data class AuthRequirements(
    val needsOnboarding: Boolean,
    val needsTermsAgreement: Boolean,
)
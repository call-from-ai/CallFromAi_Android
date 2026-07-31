package kr.co.call.domain.model.onboarding

data class OnboardingSubmission(
    val member: MemberOnboardingInput,
    val character: CharacterOnboardingInput,
)

data class MemberOnboardingInput(
    val lastName: String,
    val firstName: String,
    val imageUrl: String,
    val gender: String,
    val birth: String,
    val mbti: String,
    val job: String,
)

data class CharacterOnboardingInput(
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
    val traits: List<CharacterTraitInput>,
)

data class CharacterTraitInput(
    val trait: String,
    val priority: Int,
)
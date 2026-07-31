package kr.co.call.domain.model.onboarding

//사용자 본인의 정보
data class MemberOnboardingInput(
    val lastName: String,
    val firstName: String,
    val imageUrl: String,
    val gender: String,
    val birth: String,
    val mbti: String,
    val job: String,
)

//ai 캐릭터의 정보
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

//특성(ex.유머러스한)과 우선순위를 묶는 모델
data class CharacterTraitInput(
    val trait: String,
    val priority: Int,
)
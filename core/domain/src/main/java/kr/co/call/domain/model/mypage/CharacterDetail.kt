package kr.co.call.domain.model.mypage

/**
 * 이상형 정보 수정용 캐릭터 상세
 */
data class CharacterDetail(
    val characterId: Long,
    val lastName: String = "",
    val firstName: String = "",
    val displayName: String = "",
    val gender: String = "MALE",
    val age: Int? = null,
    val job: String? = null,
    val imageUrl: String = "",
    val spiceLevel: Int = 50,
    val preferTime: String = "ANYTIME",
    val mbti: String? = null,
    val speechStyle: String = "CASUAL",
    val relationshipStage: String = "SOME",
    val isMain: Boolean = false,
    val traits: List<CharacterTraitDetail> = emptyList(),
    val isFullDetail: Boolean = true,
)

data class CharacterTraitDetail(
    val code: String,
    val priority: Int,
)

data class CharacterUpdateInput(
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
    val traits: List<CharacterTraitDetail>,
)

package kr.co.call.domain.model.mypage

data class AiCharacter(
    val id: String= "",
    val name: String= "",
    val profileImageUrl: String= "",
    val isMain: Boolean,
    val createdAtLabel: String= "",
    val daysTogetherLabel: String= "",
    val lastConversationLabel: String= "",
    val summary: String= "",
)
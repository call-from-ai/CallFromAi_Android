package kr.co.call.impl.model

/**
 * 구독 플랜 카드에 표시할 UI 모델
 * 화면 전용 mock 데이터
 */
data class SubscriptionPlanUiModel(
    val id: String,
    val code: String,
    val name: String,
    val description: String,
    val priceLabel: String,
    val dotCount: Int,
    val badge: PlanBadgeType? = null,
    val features: List<PlanFeatureUiModel>,
)

data class PlanFeatureUiModel(
    val label: String,
    val value: String,
)

enum class PlanBadgeType {
    RECOMMENDED, // 추천 상품
    POPULAR,     // 인기 상품
}

object SubscriptionPlanMock {
    val plans: List<SubscriptionPlanUiModel> = listOf(
        SubscriptionPlanUiModel(
            id = "FREE",
            code = "FREE",
            name = "무료 플랜",
            description = "기본적인 채팅과 전화 가능",
            priceLabel = "무료",
            dotCount = 1,
            badge = PlanBadgeType.RECOMMENDED,
            features = listOf(
                PlanFeatureUiModel(label = "통화", value = "하루 1회 제한"),
                PlanFeatureUiModel(label = "관계", value = "최근 대화만 기억"),
                PlanFeatureUiModel(label = "캐릭터", value = "기본 캐릭터 (1개)"),
            ),
        ),
        SubscriptionPlanUiModel(
            id = "BASIC",
            code = "BASIC",
            name = "베이직 플랜",
            description = "더 자연스럽고 오래 이어지는 관계 가능",
            priceLabel = "월 3,900원",
            dotCount = 2,
            badge = PlanBadgeType.POPULAR,
            features = listOf(
                PlanFeatureUiModel(label = "통화", value = "무제한 통화 + AI가 먼저 전화"),
                PlanFeatureUiModel(label = "관계", value = "장기 기억 유지 + 감정 반응 강화"),
                PlanFeatureUiModel(label = "캐릭터", value = "기본 캐릭터 (1개)"),
            ),
        ),
        SubscriptionPlanUiModel(
            id = "PREMIUM",
            code = "PREMIUM",
            name = "프리미엄 플랜",
            description = "가장 현실적인 연애 체험 가능",
            priceLabel = "월 8,900원",
            dotCount = 3,
            badge = null,
            features = listOf(
                PlanFeatureUiModel(label = "통화", value = "무제한 통화 + AI가 먼저 전화 + 프리미엄 음성"),
                PlanFeatureUiModel(label = "관계", value = "장기 기억 유지 + 고급 감정 표현"),
                PlanFeatureUiModel(label = "캐릭터", value = "기본 캐릭터 (1개)"),
            ),
        ),
    )

    const val DEFAULT_CURRENT_PLAN_ID = "BASIC"
}

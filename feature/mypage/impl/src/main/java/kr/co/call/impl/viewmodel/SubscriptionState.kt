package kr.co.call.impl.viewmodel

import kr.co.call.impl.model.SubscriptionPlanMock
import kr.co.call.impl.model.SubscriptionPlanUiModel

data class SubscriptionState(
    val plans: List<SubscriptionPlanUiModel> = SubscriptionPlanMock.plans,
    val currentPlanId: String = SubscriptionPlanMock.DEFAULT_CURRENT_PLAN_ID,
    val selectedPlanId: String = SubscriptionPlanMock.DEFAULT_CURRENT_PLAN_ID,
)

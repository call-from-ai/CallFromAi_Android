package kr.co.call.impl.viewmodel

sealed interface SubscriptionIntent {
    data class SelectPlan(val planId: String) : SubscriptionIntent
    data object ClickConfirm : SubscriptionIntent
}

package kr.co.call.impl.viewmodel

sealed interface SubscriptionSideEffect {
    data object NavigateBack : SubscriptionSideEffect
    data class ShowMessage(val message: String) : SubscriptionSideEffect
    data class ConfirmPlan(val planId: String) : SubscriptionSideEffect
}

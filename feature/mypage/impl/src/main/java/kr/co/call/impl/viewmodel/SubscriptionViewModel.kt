package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor() :
    ViewModel(),
    ContainerHost<SubscriptionState, SubscriptionSideEffect> {

    override val container: Container<SubscriptionState, SubscriptionSideEffect> = container(
        initialState = SubscriptionState(),
    )

    fun handleIntent(intent: SubscriptionIntent) {
        when (intent) {
            is SubscriptionIntent.SelectPlan -> selectPlan(intent.planId)
            is SubscriptionIntent.ClickConfirm -> confirmSelection()
        }
    }

    private fun selectPlan(planId: String) = intent {
        if (state.selectedPlanId == planId) return@intent
        reduce { state.copy(selectedPlanId = planId) }
    }

    private fun confirmSelection() = intent {
        val selectedId = state.selectedPlanId
        postSideEffect(SubscriptionSideEffect.ConfirmPlan(selectedId))
    }
}

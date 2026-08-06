package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kr.co.call.domain.exception.toUserMessage
import kr.co.call.domain.repository.MyPageRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.PreferTime
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class CallTimeManagementViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
) : ViewModel(), ContainerHost<CallTimeManagementState, CallTimeManagementSideEffect> {

    override val container: Container<CallTimeManagementState, CallTimeManagementSideEffect> =
        container(initialState = CallTimeManagementState()) {
            loadPreferTime()
        }

    fun handleIntent(intent: CallTimeManagementIntent) {
        when (intent) {
            is CallTimeManagementIntent.SelectPreferTime -> selectPreferTime(intent.preferTime)
            is CallTimeManagementIntent.ClickComplete -> complete()
            is CallTimeManagementIntent.ClickBack -> navigateBack()
        }
    }

    private fun loadPreferTime() = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading) }
        myPageRepository.getPreferTime()
            .onSuccess { preferTimeName ->
                val selected = PreferTime.entries.firstOrNull {
                    it.name.equals(preferTimeName, ignoreCase = true)
                }
                reduce {
                    state.copy(
                        loadStatus = LoadStatus.Idle,
                        selected = selected,
                    )
                }
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
                reduce {
                    state.copy(
                        loadStatus = LoadStatus.Idle,
                        selected = null,
                    )
                }
            }
    }

    private fun selectPreferTime(preferTime: PreferTime) = intent {
        reduce { state.copy(selected = preferTime) }
    }

    private fun complete() = intent {
        val selected = state.selected ?: return@intent
        if (state.isSaving) return@intent

        reduce { state.copy(isSaving = true) }
        myPageRepository.updatePreferTime(selected.name)
            .onSuccess {
                reduce { state.copy(isSaving = false) }
                postSideEffect(CallTimeManagementSideEffect.NavigateBack)
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
                reduce { state.copy(isSaving = false) }
                postSideEffect(
                    CallTimeManagementSideEffect.ShowMessage(
                        error.toUserMessage(default = "전화 오는 시간 저장에 실패했습니다."),
                    ),
                )
            }
    }

    private fun navigateBack() = intent {
        postSideEffect(CallTimeManagementSideEffect.NavigateBack)
    }
}

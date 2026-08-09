package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kr.co.call.domain.repository.AgreementRepository
import kr.co.call.domain.util.LoadStatus
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class TermViewModel @Inject constructor(
    private val agreementRepository: AgreementRepository,
) : ViewModel(), ContainerHost<TermState, TermSideEffect> {

    override val container: Container<TermState, TermSideEffect> = container(
        initialState = TermState(),
    ) {
        loadTerms()
    }

    fun handleIntent(intent: TermIntent) {
        when (intent) {
            is TermIntent.Retry -> loadTerms()
        }
    }

    private fun loadTerms() = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading) }
        agreementRepository.getTerms()
            .onSuccess { terms ->
                reduce {
                    state.copy(
                        terms = terms,
                        loadStatus = LoadStatus.Idle,
                    )
                }
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
                val message = error.message ?: "약관을 불러오지 못했습니다."
                reduce { state.copy(loadStatus = LoadStatus.Error(message)) }
                postSideEffect(TermSideEffect.ShowMessage(message))
            }
    }
}

sealed interface TermIntent {
    data object Retry : TermIntent
}

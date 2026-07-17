package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.repository.FaqRepository
import kr.co.call.domain.util.LoadStatus
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FaqViewModel @Inject constructor(
    private val faqRepository: FaqRepository,
) : ViewModel(), ContainerHost<FaqState, FaqSideEffect> {

    override val container: Container<FaqState, FaqSideEffect> = container(
        initialState = FaqState()
    ) {
        loadFaqItems()
    }

    private fun loadFaqItems() = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading) }
        runCatching { faqRepository.getFaqItems() }
            .onSuccess { itemsByCategory ->
                reduce {
                    state.copy(
                        itemsByCategory = itemsByCategory,
                        loadStatus = LoadStatus.Idle,
                    )
                }
            }
            .onFailure { e ->
                val message = e.message ?: "FAQ 불러오기 실패"
                reduce { state.copy(loadStatus = LoadStatus.Error(message)) }
                postSideEffect(FaqSideEffect.ShowError(message))
            }
    }
}
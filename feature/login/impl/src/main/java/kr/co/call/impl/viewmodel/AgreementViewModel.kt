package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.model.login.TermAgreement
import kr.co.call.domain.repository.AgreementRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.viewmodel.state.AgreementUiState
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AgreementViewModel @Inject constructor(
    private val agreementRepository: AgreementRepository,
): ViewModel(),
    ContainerHost<AgreementUiState, AgreementSideEffect> {
    override val container=
        container<AgreementUiState, AgreementSideEffect>(
            initialState= AgreementUiState(),
        )

    init{
        loadTerms()
    }

    private fun loadTerms()=intent {
        reduce {state.copy(status= LoadStatus.Loading)}
            agreementRepository.getTerms()
                .onSuccess { terms ->
        reduce {
            state.copy(
                terms=terms,
                status= LoadStatus.Idle,
            )
        }
        }.onFailure { error ->
            val message=error.message ?:"약관을 불러오지 못했습니다."
        reduce{state.copy(status= LoadStatus.Error(message))}
        postSideEffect(
            AgreementSideEffect.ShowError(message),
        )
        }
    }
    //동의 개별 변경
    fun toggleAgreement(termId:Long, )=intent {
        reduce{
            state.copy(
                checkedTermIds =
                    if (termId in state.checkedTermIds){
                        state.checkedTermIds-termId
                    } else {
                        state.checkedTermIds +termId
                    },
            )
        }
    }
    fun submitAgreements()=intent{
        if(!state.isRequiredChecked || state.status== LoadStatus.Loading){
            return@intent
        }
        reduce{state.copy(status= LoadStatus.Loading)}
        val agreements=state.terms.map {term ->
            TermAgreement(
                termId=term.termId,
                isAgreed=term.termId in state.checkedTermIds,
            )
        }

            agreementRepository.agreeTerms(agreements)
            .onSuccess {
            reduce {state.copy(status= LoadStatus.Idle)}
            postSideEffect(AgreementSideEffect.NavigateToNext)
        }.onFailure { error->
            val message=error.message ?:"약관 동의에 실패했습니다."
            reduce{state.copy(status= LoadStatus.Error(message))}

            postSideEffect(
                AgreementSideEffect.ShowError(message)
            )
        }
    }

    //동의 일괄 변경
    fun toggleAllAgreements(isChecked: Boolean)= intent {
        reduce {
            state.copy(
                checkedTermIds=
                    if (isChecked){
                        state.terms
                            .map{term -> term.termId}
                            .toSet()
                    } else {
                        emptySet()
                    },
            )
        }
    }
}
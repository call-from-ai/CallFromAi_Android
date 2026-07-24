package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.repository.AgreementRepository
import kr.co.call.impl.viewmodel.state.AgreementUiState
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
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
        reduce {state.copy(isLoading = true)}
            agreementRepository.getTerms()
                .onSuccess { terms ->
                    Timber.d("약관 조회 성공: ${terms.size}개")
        reduce {
            state.copy(
                terms=terms,
                isLoading=false,
            )
        }
        }.onFailure { error ->
            Timber.e(
                error,
                "약관 조회 실패",
            )
        reduce{state.copy(isLoading = false)}
        postSideEffect(
            AgreementSideEffect.ShowError(
                message=error.message ?:"약관을 불러오지 못했습니다.",
            ),
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
        if(!state.isRequiredChecked || state.isLoading){
            return@intent
        }
        reduce{state.copy(isLoading=true)}
        val agreements=state.terms.associate {term ->
            term.termId to (term.termId in state.checkedTermIds)
        }

            agreementRepository.agreeTerms(agreements)
            .onSuccess {
            reduce {state.copy(isLoading=false)}
            postSideEffect(AgreementSideEffect.NavigateToNext)
        }.onFailure { error->
            reduce{state.copy(isLoading=false)}

            postSideEffect(
                AgreementSideEffect.ShowError(
                    message=error.message ?:"약관 동의에 실패했습니다.",
                )
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
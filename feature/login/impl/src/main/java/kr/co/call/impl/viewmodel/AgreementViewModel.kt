package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.impl.viewmodel.state.AgreementUiState
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AgreementViewModel @Inject constructor() :
    ViewModel(),
    ContainerHost<AgreementUiState, Nothing> {
    override val container=
        container<AgreementUiState, Nothing>(
            initialState= AgreementUiState(),
        )

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
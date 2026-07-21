package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.impl.screen.AgreementType
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
    fun toggleAgreement(agreementType: AgreementType)=intent {
        reduce{
            state.copy(
                checkedAgreements =
                    if (agreementType in state.checkedAgreements){
                        state.checkedAgreements-agreementType
                    } else {
                        state.checkedAgreements + agreementType
                    },
            )
        }
    }

    //동의 일괄 변경
    fun toggleAllAgreements(isChecked: Boolean)= intent {
        reduce {
            state.copy(
                checkedAgreements=
                    if (isChecked){
                        AgreementType.entries.toSet()
                    } else {
                        emptySet()
                    },
            )
        }
    }
}
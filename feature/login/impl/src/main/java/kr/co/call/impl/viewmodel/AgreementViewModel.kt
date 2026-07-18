package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kr.co.call.impl.screen.AgreementType
import javax.inject.Inject

@HiltViewModel
class AgreementViewModel @Inject constructor() : ViewModel() {
    private val mutableUiState = MutableStateFlow(AgreementUiState())
    val uiState: StateFlow<AgreementUiState> = mutableUiState.asStateFlow()
    //동의 개별 변경
    fun toggleAgreement(agreementType: AgreementType){
        mutableUiState.update {currentState ->
            currentState.copy(
                checkedAgreements=
                    if (agreementType in currentState.checkedAgreements){
                        currentState.checkedAgreements-agreementType
                    } else {
                        currentState.checkedAgreements+agreementType
                    },
            )
        }
    }
    //동의 일괄 변경
    fun toggleAllAgreements(isChecked: Boolean){
        mutableUiState.update {currentState ->
            currentState.copy(
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
package kr.co.call.impl.viewmodel

import kr.co.call.impl.screen.AgreementType

data class AgreementUiState(
    val checkedAgreements: Set<AgreementType> =emptySet(),
){
    val isAllChecked: Boolean
        get()=AgreementType.entries.all{agreementType ->
            agreementType in checkedAgreements
        }
    val isRequiredChecked: Boolean
        get()=AgreementType.entries
            .filter{agreementType ->
                agreementType.isRequired
            }
            .all{agreementType ->
                agreementType in checkedAgreements
            }
}
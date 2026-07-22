package kr.co.call.impl.viewmodel.state

import kr.co.call.domain.model.login.AgreementTerm

data class AgreementUiState(
    val terms: List<AgreementTerm> = emptyList(),
    val checkedTermIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
){
    val isAllChecked: Boolean
        get() = terms.isNotEmpty() &&
                terms.all { it.termId in checkedTermIds }

    val isRequiredChecked: Boolean
        get() = terms
            .filter { it.isRequired }
            .all { it.termId in checkedTermIds }
}
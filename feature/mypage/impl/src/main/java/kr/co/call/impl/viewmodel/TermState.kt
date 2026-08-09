package kr.co.call.impl.viewmodel

import kr.co.call.domain.model.login.AgreementTerm
import kr.co.call.domain.util.LoadStatus

data class TermState(
    val terms: List<AgreementTerm> = emptyList(),
    val loadStatus: LoadStatus = LoadStatus.Idle,
)

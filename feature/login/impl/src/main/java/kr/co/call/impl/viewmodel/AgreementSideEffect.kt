package kr.co.call.impl.viewmodel

sealed interface AgreementSideEffect{
    data object NavigateToNext: AgreementSideEffect
    data class ShowError(val message: String):AgreementSideEffect
}
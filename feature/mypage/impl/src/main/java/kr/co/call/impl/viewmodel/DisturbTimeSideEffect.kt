package kr.co.call.impl.viewmodel

sealed interface DisturbTimeSideEffect {
    data object NavigateBack : DisturbTimeSideEffect
    data class ShowMessage(val message: String) : DisturbTimeSideEffect
}

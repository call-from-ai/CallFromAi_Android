package kr.co.call.impl.viewmodel

sealed interface CallTimeManagementSideEffect {
    data object NavigateBack : CallTimeManagementSideEffect
    data class ShowMessage(val message: String) : CallTimeManagementSideEffect
}

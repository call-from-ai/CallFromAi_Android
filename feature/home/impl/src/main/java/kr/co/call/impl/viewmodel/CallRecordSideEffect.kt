package kr.co.call.impl.viewmodel

sealed interface CallRecordSideEffect {

    data class ShowMessage(
        val message: String,
    ) : CallRecordSideEffect
}

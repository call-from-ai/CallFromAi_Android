package kr.co.call.impl.viewmodel

sealed interface CallRecordIntent {

    data class Load(
        val callId: Long,
    ) : CallRecordIntent

    data object Retry : CallRecordIntent
}

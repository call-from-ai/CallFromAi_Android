package kr.co.call.domain.repository

interface CallControlRepository {
    suspend fun startCall(): Long
    suspend fun acceptCall(callId: Long)
    suspend fun rejectCall(callId: Long)
    suspend fun endCall(callId: Long)
}

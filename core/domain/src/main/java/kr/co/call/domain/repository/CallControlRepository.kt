package kr.co.call.domain.repository

interface CallControlRepository {
    suspend fun startCall(): String
    suspend fun acceptCall(callId: String)
    suspend fun rejectCall(callId: String)
    suspend fun endCall(callId: String)
}
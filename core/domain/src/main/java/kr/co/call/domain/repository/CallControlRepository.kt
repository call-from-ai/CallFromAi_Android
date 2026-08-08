package kr.co.call.domain.repository

import kr.co.call.domain.model.call.CallConnectionInfo
import kr.co.call.domain.model.call.CallEndInfo
import kr.co.call.domain.model.call.IncomingCall

interface CallControlRepository {
    suspend fun getIncomingCall(): IncomingCall?

    suspend fun startCall(characterId: Long): CallConnectionInfo

    suspend fun acceptCall(callId: Long): CallConnectionInfo

    suspend fun rejectCall(callId: Long)

    suspend fun endCall(callId: Long): CallEndInfo
}

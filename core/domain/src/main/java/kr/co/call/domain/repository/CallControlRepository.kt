package kr.co.call.domain.repository

import kr.co.call.domain.model.call.ActiveCharacter
import kr.co.call.domain.model.call.CallConnectionInfo
import kr.co.call.domain.model.call.CallEndInfo
import kr.co.call.domain.model.call.IncomingCall

interface CallControlRepository {
    suspend fun getIncomingCall(): IncomingCall?

    suspend fun startCall(characterId: Long): CallConnectionInfo

    suspend fun acceptCall(callId: Long): CallConnectionInfo

    suspend fun rejectCall(callId: Long)

    suspend fun endCall(callId: Long): CallEndInfo

    // characterId로 캐릭터 상세 조회 (통화 화면 이름·사진 표시용)
    suspend fun getCharacter(characterId: Long): ActiveCharacter
}

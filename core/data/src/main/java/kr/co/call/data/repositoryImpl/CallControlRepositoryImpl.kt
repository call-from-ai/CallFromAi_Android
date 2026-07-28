package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.domain.repository.CallControlRepository

// TODO: 실제 통화 제어(시그널링) 연동 전까지의 임시 구현
class CallControlRepositoryImpl @Inject constructor() : CallControlRepository {

    override suspend fun startCall(): Long = DUMMY_CALL_ID

    override suspend fun acceptCall(callId: Long) = Unit

    override suspend fun rejectCall(callId: Long) = Unit

    override suspend fun endCall(callId: Long) = Unit

    private companion object {
        const val DUMMY_CALL_ID = 1L
    }
}

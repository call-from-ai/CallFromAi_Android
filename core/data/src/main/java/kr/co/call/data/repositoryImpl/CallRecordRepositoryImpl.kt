package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.domain.model.home.CallTranscript
import kr.co.call.domain.repository.CallRecordRepository

class CallRecordRepositoryImpl @Inject constructor() : CallRecordRepository {

    override suspend fun getCallInfo(callId: Long): Result<CallInfo> =
        unsupportedApi()

    override suspend fun getCallScript(
        callId: Long,
    ): Result<List<CallTranscript>> = unsupportedApi()

    private fun <T> unsupportedApi(): Result<T> =
        Result.failure(
            UnsupportedOperationException("통화 기록 서버 API가 아직 연결되지 않았습니다."),
        )
}

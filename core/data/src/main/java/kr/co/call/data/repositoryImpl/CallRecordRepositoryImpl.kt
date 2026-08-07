package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.data.mapper.toDomain
import kr.co.call.data.util.runRepositoryCatching
import kr.co.call.domain.model.call.CallListItem
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.domain.model.home.CallTranscript
import kr.co.call.domain.repository.CallRecordRepository
import kr.co.call.network.api.CallApi
import kr.co.call.network.util.ErrorResponseParser
import kr.co.call.network.util.safeApiCall

/**
 * 통화 목록·상세·전사 API를 Domain 모델로 변환합니다.
 */
class CallRecordRepositoryImpl @Inject constructor(
    private val callApi: CallApi,
    private val errorResponseParser: ErrorResponseParser,
) : CallRecordRepository {

    override suspend fun getCalls(): Result<List<CallListItem>> =
        runRepositoryCatching {
            safeApiCall(errorResponseParser) {
                callApi.getCalls()
            }.content.map { call ->
                call.toDomain()
            }
        }

    override suspend fun getCallInfo(callId: Long, wait: Boolean): Result<CallInfo> =
        runRepositoryCatching {
            safeApiCall(errorResponseParser) {
                callApi.getCall(callId, wait = wait)
            }.toDomain(callId)
        }

    override suspend fun getCallScript(
        callId: Long,
    ): Result<List<CallTranscript>> =
        runRepositoryCatching {
            safeApiCall(errorResponseParser) {
                callApi.getCallScript(callId)
            }.toDomain()
        }
}

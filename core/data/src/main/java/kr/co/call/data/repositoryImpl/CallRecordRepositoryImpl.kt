package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.data.mapper.toDomain
import kr.co.call.data.util.safeApiResult
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.domain.model.home.CallTranscript
import kr.co.call.domain.repository.CallRecordRepository
import kr.co.call.network.api.HomeApi
import kr.co.call.network.util.ErrorResponseParser
import timber.log.Timber

class CallRecordRepositoryImpl @Inject constructor(
    private val homeApi: HomeApi,
    private val errorResponseParser: ErrorResponseParser,
) : CallRecordRepository {

    override suspend fun getCallInfo(callId: Long): Result<CallInfo> =
        safeApiResult(errorResponseParser) {
            homeApi.getCallDetail(callId)
        }
            .map { callDetail -> callDetail.toDomain(callId) }
            .onSuccess { callInfo ->
                Timber.tag(TAG).d(
                    "통화 상세 조회 response: callId=%d, recordingUrl=%s",
                    callInfo.callId,
                    callInfo.recordingUrl,
                )
            }

    override suspend fun getCallScript(
        callId: Long,
    ): Result<List<CallTranscript>> =
        safeApiResult(errorResponseParser) {
            homeApi.getTranscript(callId)
        }
            .mapCatching { transcript -> transcript.toDomain() }
            .onSuccess { transcripts ->
                Timber.tag(TAG).d(
                    "통화 스크립트 조회 response: callId=%d, count=%d",
                    callId,
                    transcripts.size,
                )
            }

    private companion object {
        const val TAG = "CallRecordRepository"
    }
}

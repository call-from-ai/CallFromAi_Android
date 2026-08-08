package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.data.mapper.toDomain
import kr.co.call.data.util.runRepositoryCatching
import kr.co.call.data.util.safeApiResult
import kr.co.call.data.util.safeApiResultUnit
import kr.co.call.domain.model.call.CallConnectionInfo
import kr.co.call.domain.model.call.CallEndInfo
import kr.co.call.domain.model.call.IncomingCall
import kr.co.call.domain.repository.CallControlRepository
import kr.co.call.network.api.CallApi
import kr.co.call.network.dto.call.DialCallRequestDto
import kr.co.call.network.util.ErrorResponseParser
import kr.co.call.network.util.safeApiCallNullable

/**
 * 통화 발신·착신 제어 API를 Domain 모델로 변환합니다.
 */
class CallControlRepositoryImpl @Inject constructor(
    private val callApi: CallApi,
    private val errorResponseParser: ErrorResponseParser,
) : CallControlRepository {

    // 착신 대기중인 목록 조회
    override suspend fun getIncomingCall(): IncomingCall? =
        runRepositoryCatching {
            safeApiCallNullable(errorResponseParser) {
                callApi.getIncomingCall()
            }?.toDomain()
        }.getOrThrow()

    // 통화 발신
    override suspend fun startCall(characterId: Long): CallConnectionInfo =
        safeApiResult(errorResponseParser) {
            callApi.dialCall(
                request = DialCallRequestDto(
                    characterId = characterId,
                ),
            )
        }.map { it.toDomain() }.getOrThrow()

    // 통화 수락
    override suspend fun acceptCall(callId: Long): CallConnectionInfo =
        safeApiResult(errorResponseParser) {
            callApi.acceptCall(callId)
        }.map { it.toDomain() }.getOrThrow()

    // 통화 거절
    override suspend fun rejectCall(callId: Long) {
        safeApiResultUnit(errorResponseParser) {
            callApi.rejectCall(callId)
        }.getOrThrow()
    }

    // 통화 종료
    override suspend fun endCall(callId: Long): CallEndInfo =
        safeApiResult(errorResponseParser) {
            callApi.endCall(callId)
        }.map { it.toDomain() }.getOrThrow()
}

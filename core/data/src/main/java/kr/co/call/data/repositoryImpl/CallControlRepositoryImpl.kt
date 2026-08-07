package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.data.mapper.toDomain
import kr.co.call.data.util.runRepositoryCatching
import kr.co.call.domain.model.call.ActiveCharacter
import kr.co.call.domain.model.call.CallConnectionInfo
import kr.co.call.domain.model.call.CallEndInfo
import kr.co.call.domain.model.call.IncomingCall
import kr.co.call.domain.repository.CallControlRepository
import kr.co.call.network.api.CallApi
import kr.co.call.network.dto.call.DialCallRequestDto
import kr.co.call.network.util.ErrorResponseParser
import kr.co.call.network.util.safeApiCall
import kr.co.call.network.util.safeApiCallNullable
import kr.co.call.network.util.safeApiCallUnit

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
        runRepositoryCatching {
            safeApiCall(errorResponseParser) {
                callApi.dialCall(
                    request = DialCallRequestDto(
                        characterId = characterId,
                    ),
                )
            }.toDomain()
        }.getOrThrow()

    // 통화 수락
    override suspend fun acceptCall(callId: Long): CallConnectionInfo =
        runRepositoryCatching {
            safeApiCall(errorResponseParser) {
                callApi.acceptCall(callId)
            }.toDomain()
        }.getOrThrow()

    // 통화 거절
    override suspend fun rejectCall(callId: Long) {
        runRepositoryCatching {
            safeApiCallUnit(errorResponseParser) {
                callApi.rejectCall(callId)
            }
        }.getOrThrow()
    }

    // 통화 종료
    override suspend fun endCall(callId: Long): CallEndInfo =
        runRepositoryCatching {
            safeApiCall(errorResponseParser) {
                callApi.endCall(callId)
            }.toDomain()
        }.getOrThrow()

    // characterId로 캐릭터 상세 조회
    // 통화 화면에 표시할 이름·사진에 사용
    override suspend fun getCharacter(characterId: Long): ActiveCharacter =
        runRepositoryCatching {
            safeApiCall(errorResponseParser) {
                callApi.getCharacter(characterId)
            }.toDomain()
        }.getOrThrow()
}

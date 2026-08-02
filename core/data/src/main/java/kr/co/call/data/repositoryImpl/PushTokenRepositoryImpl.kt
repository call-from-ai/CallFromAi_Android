package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.data.util.safeApiResultUnit
import kr.co.call.domain.repository.PushTokenRepository
import kr.co.call.network.api.PushTokenApi
import kr.co.call.network.dto.push.PLATFORM_ANDROID
import kr.co.call.network.dto.push.PushTokenDeleteRequestDto
import kr.co.call.network.dto.push.PushTokenRegisterRequestDto
import kr.co.call.network.util.ErrorResponseParser

/**
 * 푸시 토큰 API 구현
 * AuthInterceptor 가 JWT 를 붙이므로 로그인된 상태에서만 호출해야 한다.
 */
class PushTokenRepositoryImpl @Inject constructor(
    private val pushTokenApi: PushTokenApi,
    private val errorResponseParser: ErrorResponseParser,
) : PushTokenRepository {

    override suspend fun register(token: String): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            require(token.isNotBlank()) { "FCM 토큰이 비어 있습니다" }
            pushTokenApi.register(
                request = PushTokenRegisterRequestDto(
                    token = token,
                    platform = PLATFORM_ANDROID,
                ),
            )
        }

    override suspend fun delete(token: String): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            require(token.isNotBlank()) { "FCM 토큰이 비어 있습니다" }
            pushTokenApi.delete(
                request = PushTokenDeleteRequestDto(token = token),
            )
        }
}

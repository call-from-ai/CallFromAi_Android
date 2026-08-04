package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.push.PushTokenDeleteRequestDto
import kr.co.call.network.dto.push.PushTokenRegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

/**
 * FCM 디바이스 토큰 등록/삭제 API
 * JWT 인증 필요 (AuthInterceptor)
 *
 * - 등록: 로그인 후 / 토큰 갱신 시
 * - 삭제: 로그아웃 시
 */
interface PushTokenApi {

    @POST("push-tokens")
    suspend fun register(
        @Body request: PushTokenRegisterRequestDto,
    ): ApiResponse<Unit>

    @HTTP(method = "DELETE", path = "push-tokens", hasBody = true)
    suspend fun delete(
        @Body request: PushTokenDeleteRequestDto,
    ): ApiResponse<Unit>
}

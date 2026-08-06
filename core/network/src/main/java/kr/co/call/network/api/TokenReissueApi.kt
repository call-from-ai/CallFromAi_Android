package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.login.LoginTokenResult
import kr.co.call.network.dto.login.TokenReissueRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Access Token이 만료됐을 때 새로운 토큰을 발급받는 Retrofit API
 * 재발급은 영어로 reissue이며 기존 토큰을 새 토큰으로 교체하는 과정이다.
 */
interface TokenReissueApi {
    @POST("auth/refresh")
    suspend fun reissue(
        @Body request: TokenReissueRequestDto,
    ): ApiResponse<LoginTokenResult>
}
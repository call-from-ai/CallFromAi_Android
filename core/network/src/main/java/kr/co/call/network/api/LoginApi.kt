package kr.co.call.network.api

import kr.co.call.network.dto.login.LoginRequestDto
import kr.co.call.network.dto.login.LoginResponseDto
import kr.co.call.network.dto.login.TokenReissueRequestDto
import kr.co.call.network.dto.login.TokenReissueResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 카카오 로그인 정보를 서버에 전달하는 Retrofit API
 * 카카오 Access Token을 보내고 서비스용 인증 토큰을 발급받는다.
 */

interface LoginApi {
    @POST("auth/kakao")
    suspend fun login(
        @Body request: LoginRequestDto,
    ): LoginResponseDto
}
package kr.co.call.network.api

import kr.co.call.network.dto.login.LoginRequestDto
import kr.co.call.network.dto.login.LoginResponseDto
import kr.co.call.network.dto.login.TokenReissueRequestDto
import kr.co.call.network.dto.login.TokenReissueResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("auth/kakao")
    suspend fun login(
        @Body request: LoginRequestDto,
    ): LoginResponseDto
}
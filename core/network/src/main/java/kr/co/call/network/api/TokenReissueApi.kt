package kr.co.call.network.api

import kr.co.call.network.dto.login.TokenReissueRequestDto
import kr.co.call.network.dto.login.TokenReissueResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

//액세스 토큰 만료 시 새 토큰 발급받는 api
interface TokenReissueApi {
    @POST("auth/reissue")
    suspend fun reissue(
        @Body request: TokenReissueRequestDto,
    ): Response<TokenReissueResponseDto>
}
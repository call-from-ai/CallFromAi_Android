package kr.co.call.network.api

import kr.co.call.network.dto.login.AgreeTermsRequestDto
import kr.co.call.network.dto.login.AgreeTermsResponseDto
import kr.co.call.network.dto.login.GetTermsResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AgreementApi {
    @GET("terms")
    suspend fun getTerms(): GetTermsResponseDto

    @POST("members/me/terms")
    suspend fun agreeTerms(
        @Body request: AgreeTermsRequestDto,
    ): AgreeTermsResponseDto
}
package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.login.AgreeTermsRequestDto
import kr.co.call.network.dto.login.TermDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AgreementApi {
    @GET("terms")
    suspend fun getTerms(): ApiResponse<List<TermDto>>

    @POST("members/me/terms")
    suspend fun agreeTerms(
        @Body request: AgreeTermsRequestDto,
    ): ApiResponse<Unit>
}
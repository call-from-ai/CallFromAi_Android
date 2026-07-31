package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.onboarding.UpdateMemberRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST

interface MyPageApi {
    @POST("auth/logout")
    suspend fun logout()

    @DELETE("members/me")
    suspend fun deleteAccount(): ApiResponse<Any>

    @PATCH("members/me")
    suspend fun updateMember(
        @Body request: UpdateMemberRequestDto,
    ): ApiResponse<Any>
}

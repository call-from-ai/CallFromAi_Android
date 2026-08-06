package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.mypage.MemberResponseDto
import kr.co.call.network.dto.onboarding.UpdateMemberRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface MyPageApi {
    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Any>

    @DELETE("members/me")
    suspend fun deleteAccount(): ApiResponse<Any>

    @POST("members/me")
    suspend fun createMember(
        @Body request: UpdateMemberRequestDto,
    ): ApiResponse<Any>

    @PATCH("members/me")
    suspend fun updateMember(
        @Body request: UpdateMemberRequestDto,
    ): ApiResponse<Any>

    @GET("members/me")
    suspend fun getMyInfo():ApiResponse<MemberResponseDto>
}

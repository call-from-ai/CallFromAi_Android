package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.mypage.DoNotDisturbUpdateRequestDto
import kr.co.call.network.dto.mypage.MemberResponseDto
import kr.co.call.network.dto.mypage.NotificationSettingDto
import kr.co.call.network.dto.mypage.NotificationSettingUpdateRequestDto
import kr.co.call.network.dto.onboarding.UpdateMemberRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface MyPageApi {
    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Any>

    @GET("members/me")
    suspend fun getMyInfo(): ApiResponse<MemberResponseDto>

    @DELETE("members/me")
    suspend fun deleteAccount(): ApiResponse<Any>

    @POST("members/me")
    suspend fun createMember(
        @Body request: UpdateMemberRequestDto,
    ): ApiResponse<Any>

    @PATCH("members/me")
    suspend fun updateMember(
        @Body request: UpdateMemberRequestDto,
    ): ApiResponse<MemberResponseDto>

    @GET("members/me/notification-settings")
    suspend fun getNotificationSettings(): ApiResponse<NotificationSettingDto>

    @PATCH("members/me/notification-settings")
    suspend fun updateNotificationSettings(
        @Body request: NotificationSettingUpdateRequestDto,
    ): ApiResponse<NotificationSettingDto>

    @PATCH("members/me/notification-settings/do-not-disturb")
    suspend fun updateDoNotDisturb(
        @Body request: DoNotDisturbUpdateRequestDto,
    ): ApiResponse<NotificationSettingDto>

    @DELETE("members/me/notification-settings/do-not-disturb")
    suspend fun deleteDoNotDisturb(): ApiResponse<NotificationSettingDto>

}

package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST

interface MyPageApi {
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @DELETE("members/me")
    suspend fun deleteAccount(): ApiResponse<Any>
}
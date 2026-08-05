package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.onboarding.PresetImageResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PresetImageApi {
    @GET("preset-images")
    suspend fun getPresetImages(
        @Query("gender") gender: String,
    ): ApiResponse<List<PresetImageResponseDto>>
}
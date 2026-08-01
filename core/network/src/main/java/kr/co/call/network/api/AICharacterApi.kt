package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.onboarding.CreateCharacterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AICharacterApi {
    @POST("characters")
    suspend fun createCharacter(
        @Body request: CreateCharacterRequestDto,
    ): ApiResponse<Any>
}
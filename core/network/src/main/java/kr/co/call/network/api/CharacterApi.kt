package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.character.ActiveCharacterDto
import kr.co.call.network.dto.character.ChatSummaryDto
import kr.co.call.network.dto.character.MyCharacterDto
import kr.co.call.network.dto.character.UpdateCharacterRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface CharacterApi {
    @GET("characters")
    suspend fun getCharacters(): ApiResponse<List<MyCharacterDto>>

    @GET("characters/active")
    suspend fun getActiveCharacter(): ApiResponse<ActiveCharacterDto>

    @GET("characters/{characterId}/chat-summary")
    suspend fun getChatSummary(
        @Path("characterId") characterId: Long,
    ): ApiResponse<ChatSummaryDto>

    @PATCH("characters/{characterId}")
    suspend fun updateCharacter(
        @Path("characterId") characterId: Long,
        @Body request: UpdateCharacterRequestDto,
    ): ApiResponse<Any>

    @PATCH("characters/{characterId}/activate")
    suspend fun activateCharacter(
        @Path("characterId") characterId: Long,
    ): ApiResponse<Unit>

    @DELETE("characters/{characterId}")
    suspend fun deleteCharacter(
        @Path("characterId") characterId: Long,
    ): ApiResponse<Any>
}

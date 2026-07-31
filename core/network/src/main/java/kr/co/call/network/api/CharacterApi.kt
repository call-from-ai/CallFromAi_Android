package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.character.MyCharacterDto
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface CharacterApi {
    @GET("characters")
    suspend fun getCharacters(): ApiResponse<List<MyCharacterDto>>

    @PATCH("characters/{characterId}/activate")
    suspend fun activateCharacter(
        @Path("characterId") characterId: Long,
    ): ApiResponse<Unit>
}

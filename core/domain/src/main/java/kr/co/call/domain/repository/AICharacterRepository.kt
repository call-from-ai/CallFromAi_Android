package kr.co.call.domain.repository

import kr.co.call.domain.model.mypage.AiCharacter

interface AICharacterRepository {
    suspend fun getCharacters(): Result<List<AiCharacter>>
    suspend fun deleteCharacter(characterId: String): Result<Unit>
    suspend fun canAddCharacter(): Result<Boolean>
}
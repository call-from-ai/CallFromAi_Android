package kr.co.call.domain.repository

import kr.co.call.domain.model.mypage.AiCharacter

interface AICharacterRepository {
    suspend fun getCharacters(): List<AiCharacter>
    suspend fun deleteCharacter(characterId: String)
    suspend fun canAddCharacter(): Boolean
}
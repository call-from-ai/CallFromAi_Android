package kr.co.call.domain.repository

import kr.co.call.domain.model.mypage.AiCharacter
import kr.co.call.domain.model.mypage.CharacterDetail
import kr.co.call.domain.model.mypage.CharacterUpdateInput

interface AICharacterRepository {
    suspend fun getCharacters(): Result<List<AiCharacter>>

    suspend fun getCharacterDetail(characterId: Long): Result<CharacterDetail>

    suspend fun updateCharacter(
        characterId: Long,
        input: CharacterUpdateInput,
    ): Result<Unit>

    suspend fun deleteCharacter(characterId: String): Result<Unit>

    suspend fun canAddCharacter(): Result<Boolean>

    suspend fun getChatSummary(characterId: Long): Result<String>
}

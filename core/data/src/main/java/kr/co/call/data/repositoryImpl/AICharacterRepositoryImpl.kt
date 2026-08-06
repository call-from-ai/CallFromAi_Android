package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.data.mapper.toAiCharacter
import kr.co.call.data.mapper.toCharacterDetail
import kr.co.call.data.mapper.toRequestDto
import kr.co.call.data.util.safeApiResult
import kr.co.call.data.util.safeApiResultUnit
import kr.co.call.domain.exception.AppException
import kr.co.call.domain.model.mypage.AiCharacter
import kr.co.call.domain.model.mypage.CharacterDetail
import kr.co.call.domain.model.mypage.CharacterUpdateInput
import kr.co.call.domain.repository.AICharacterRepository
import kr.co.call.network.api.CharacterApi
import kr.co.call.network.util.ErrorResponseParser

class AICharacterRepositoryImpl @Inject constructor(
    private val characterApi: CharacterApi,
    private val errorResponseParser: ErrorResponseParser,
) : AICharacterRepository {

    override suspend fun getCharacters(): Result<List<AiCharacter>> =
        safeApiResult(errorResponseParser) {
            characterApi.getCharacters()
        }.map { list -> list.map { it.toAiCharacter() } }

    override suspend fun getCharacterDetail(characterId: Long): Result<CharacterDetail> =
        safeApiResult(errorResponseParser) {
            characterApi.getCharacter(characterId)
        }.map { dto -> dto.toCharacterDetail() }

    override suspend fun updateCharacter(
        characterId: Long,
        input: CharacterUpdateInput,
    ): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            characterApi.updateCharacter(
                characterId = characterId,
                request = input.toRequestDto(),
            )
        }

    override suspend fun deleteCharacter(characterId: String): Result<Unit> {
        val id = characterId.toLongOrNull()
            ?: return Result.failure(
                AppException.Business(
                    code = "CHARACTER400",
                    message = "잘못된 캐릭터입니다.",
                ),
            )
        return safeApiResultUnit(errorResponseParser) {
            characterApi.deleteCharacter(id)
        }
    }

    override suspend fun canAddCharacter(): Result<Boolean> {
        // TODO: 마지막 캐릭터 생성 시각 기준 24시간 경과 여부 (백엔드)
        return runCatching { true }
    }

    override suspend fun getChatSummary(characterId: Long): Result<String> =
        safeApiResult(errorResponseParser) {
            characterApi.getChatSummary(characterId)
        }.map { dto -> dto.summary }
}


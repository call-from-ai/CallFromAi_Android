package kr.co.call.data.repositoryImpl

import kr.co.call.domain.model.mypage.AiCharacter
import kr.co.call.domain.repository.AICharacterRepository
import javax.inject.Inject

// TODO: API 연동 전 임시 구현체
class AICharacterRepositoryImpl @Inject constructor() : AICharacterRepository {

    private val aiCharacters = mutableListOf(
        AiCharacter(
            id = "1", name = "김민준", profileImageUrl = "", isMain = true,
            createdAtLabel = "2025.05.26", daysTogetherLabel = "35일 째", lastConversationLabel = "오늘 21:42",
            summary = "아이스크림을 커피보다 좋아하며, 늦은 저녁 시간에 대화하는 것을 선호해요. 디자인과 여행 이야기에 관심이 많고, 반말대를 편하게 여겨요.",
        ),
        AiCharacter(
            id = "2", name = "박동휘", profileImageUrl = "", isMain = false,
            createdAtLabel = "2025.06.19", daysTogetherLabel = "11일 째", lastConversationLabel = "6/29 12:30",
            summary = "운동과 액티비티를 좋아하고, 아침 시간대 대화를 선호해요.",
        ),
        AiCharacter(
            id = "3", name = "정유나", profileImageUrl = "", isMain = false,
            createdAtLabel = "2025.06.25", daysTogetherLabel = "5일 째", lastConversationLabel = "6/28 09:04",
            summary = "잔잔한 음악과 독서를 좋아하고, 차분한 대화를 선호해요.",
        ),
    )

    override suspend fun getCharacters(): Result<List<AiCharacter>>{
        return runCatching {
            aiCharacters.toList()
        }
    }

    override suspend fun deleteCharacter(characterId: String): Result<Unit> {
        return runCatching {
            aiCharacters.removeAll { it.id == characterId }
            Unit
        }
    }

    override suspend fun canAddCharacter(): Result<Boolean> {
        // TODO: 마지막 캐릭터 생성 시각 기준 24시간 경과 여부를 판단(백엔드)
        return runCatching { false }
    }
}
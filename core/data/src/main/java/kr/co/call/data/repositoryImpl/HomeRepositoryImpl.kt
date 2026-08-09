package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.data.mapper.toDomain
import kr.co.call.data.mapper.toHomeCharacter
import kr.co.call.data.util.safeApiResult
import kr.co.call.data.util.safeApiResultUnit
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.HomeCharacter
import kr.co.call.domain.model.home.HomeNotification
import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.network.api.CharacterApi
import kr.co.call.network.api.HomeApi
import kr.co.call.network.util.ErrorResponseParser
import timber.log.Timber

/**
 * 홈 화면 관련 Repository 구현체입니다.
 * - 마이페이지에서 공통 호출하는 캐릭터 관련 api도 주입해서 처리합니다.
 */
class HomeRepositoryImpl @Inject constructor(
    private val homeApi: HomeApi,
    private val characterApi: CharacterApi,
    private val errorResponseParser: ErrorResponseParser,
) : HomeRepository {

    override suspend fun getCharacters(): Result<List<HomeCharacter>> =
        safeApiResult(errorResponseParser) {
            characterApi.getCharacters()
        }
            .map { characters ->
                characters.map { character -> character.toHomeCharacter() }
            }
            .onSuccess { characters ->
                Timber.tag(TAG).d(
                    "캐릭터 목록 조회 response: count=%d, mainExists=%s",
                    characters.size,
                    characters.any { character -> character.isMain },
                )
            }

    override suspend fun getCallHistories(): Result<List<CallHistory>> =
        safeApiResult(errorResponseParser) {
            homeApi.getCallHistories()
        }
        .mapCatching { page ->
            page.content.map { callHistory ->
                callHistory.toDomain()
            }
        }
            .onSuccess { callHistories ->
                Timber.tag(TAG).d(
                    "통화 기록: %s",
                    callHistories.joinToString { callHistory ->
                        "id=${callHistory.callId}, isMissed=${callHistory.isMissed}"
                    },
                )
            }

    override suspend fun getSummary(): Result<HomeSummary> =
        safeApiResult(errorResponseParser) {
            homeApi.getSummary()
        }
            .map { summary -> summary.toDomain() }
            .onSuccess { summary ->
                Timber.tag(TAG).d(
                    "관계 요약 조회 response: relationshipDays=%d, totalCallCount=%d, callStreakDays=%d",
                    summary.relationshipDays,
                    summary.totalCallCount,
                    summary.callStreakDays,
                )
            }

    override suspend fun activateCharacter(
        characterId: Long,
    ): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            characterApi.activateCharacter(characterId)
        }
            .onSuccess {
                Timber.tag(TAG).d(
                    "활성 캐릭터 response: characterId=%d, success=true",
                    characterId,
                )
            }

    override suspend fun getNotifications(): Result<List<HomeNotification>> =
        safeApiResult(errorResponseParser) {
            homeApi.getNotifications()
        }
            .map { notifications ->
                notifications.map { notification -> notification.toDomain() }
            }
            .onSuccess { notifications ->
                Timber.tag(TAG).d(
                    "지난 알림 목록 조회 response: count=%d",
                    notifications.size,
                )
            }

    override suspend fun readAllNotifications(): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            homeApi.readAllNotifications()
        }
            .onSuccess {
                Timber.tag(TAG).d("전체 알림 읽음 처리 response: success=true")
            }

    private companion object {
        const val TAG = "HomeRepository"
    }
}

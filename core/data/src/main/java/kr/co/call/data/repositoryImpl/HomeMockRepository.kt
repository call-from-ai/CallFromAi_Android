package kr.co.call.data.repositoryImpl

import java.time.LocalDateTime
import javax.inject.Inject
import kr.co.call.domain.exception.CharacterChangeUnavailableException
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.CallReservation
import kr.co.call.domain.model.home.CallReservations
import kr.co.call.domain.model.home.HomeCharacter
import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.domain.repository.HomeRepository

class HomeMockRepository @Inject constructor() : HomeRepository {

    private var characters = listOf(
        HomeCharacter(
            id = 1L,
            name = "민준",
            relationshipDays = 30,
            imageUrl = null,
            isMain = true,
        ),
        HomeCharacter(
            id = 2L,
            name = "동휘",
            relationshipDays = 12,
            imageUrl = null,
            isMain = false,
        ),
    )

    private var reservations = CallReservations(
        totalCount = 1,
        items = listOf(
            CallReservation(
                id = 3L,
                characterId = 1L,
                firstName = "민준",
                imageUrl = null,
                scheduledAt = LocalDateTime.of(2026, 6, 30, 21, 0),
            ),
        ),
    )

    override suspend fun getCharacters(): Result<List<HomeCharacter>> =
        Result.success(characters)

    override suspend fun getReservations(): Result<CallReservations> =
        Result.success(reservations)

    override suspend fun getCallHistories(): Result<List<CallHistory>> =
        Result.success(
            List(20) { index ->
                CallHistory(
                    callId = (index + 1).toLong(),
                    characterName = if (index % 2 == 0) "민준" else "동휘",
                    aiSummary = if (index % 2 == 0) {
                        "오늘 하루와 퇴근 후 일상 이야기"
                    } else {
                        "몸살 감기 기운과 걱정해주는 이야기"
                    },
                    startedAt = LocalDateTime.of(
                        2026,
                        6,
                        (28 - index).coerceAtLeast(1),
                        23,
                        2,
                    ),
                    isOutgoing = index % 2 == 0,
                    isMissed = index % 4 == 3,
                )
            },
        )

    override suspend fun getSummary(): Result<HomeSummary> =
        Result.success(
            HomeSummary(
                firstName = "수현",
                relationshipDays = 30,
                totalCallCount = 24,
                callStreakDays = 12,
            ),
        )

    override suspend fun changeReservationTime(
        reservationId: Long,
        scheduledAt: LocalDateTime,
    ): Result<CallReservations> {
        val hasReservation = reservations.items.any { reservation ->
            reservation.id == reservationId
        }
        if (!hasReservation) {
            return Result.failure(
                IllegalArgumentException("변경할 예약을 찾을 수 없습니다."),
            )
        }

        reservations = reservations.copy(
            items = reservations.items.map { reservation ->
                if (reservation.id == reservationId) {
                    reservation.copy(scheduledAt = scheduledAt)
                } else {
                    reservation
                }
            },
        )

        return Result.success(reservations)
    }

    override suspend fun changeMainCharacter(
        characterId: Long,
    ): Result<List<HomeCharacter>> {
        val hasCharacter = characters.any { character ->
            character.id == characterId
        }
        if (!hasCharacter) {
            return Result.failure(
                IllegalArgumentException("변경할 캐릭터를 찾을 수 없습니다."),
            )
        }

        return Result.failure(CharacterChangeUnavailableException())
    }

    override suspend fun startCall(characterId: Long): Result<Unit> {
        val isMainCharacter = characters.any { character ->
            character.id == characterId && character.isMain
        }

        return if (isMainCharacter) {
            Result.success(Unit)
        } else {
            Result.failure(
                IllegalStateException("메인 캐릭터와만 통화할 수 있습니다."),
            )
        }
    }
}

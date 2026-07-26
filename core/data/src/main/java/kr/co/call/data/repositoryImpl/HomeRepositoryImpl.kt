package kr.co.call.data.repositoryImpl

import java.time.LocalDateTime
import javax.inject.Inject
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.CallReservation
import kr.co.call.domain.model.home.CallReservations
import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.domain.repository.HomeRepository

class HomeRepositoryImpl @Inject constructor() : HomeRepository {

    override suspend fun getReservations(): Result<CallReservations> =
        Result.success(
            CallReservations(
                totalCount = 1,
                items = listOf(
                    CallReservation(
                        id = 3L,
                        characterId = 2L,
                        firstName = "민준",
                        imageUrl = null,
                        scheduledAt = LocalDateTime.of(2026, 6, 30, 21, 0),
                    ),
                ),
            ),
        )

    override suspend fun getCallHistories(): Result<List<CallHistory>> =
        Result.success(
            List(CALL_HISTORY_SIZE) { index ->
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
    ): Result<CallReservations> =
        Result.success(
            CallReservations(
                totalCount = 1,
                items = listOf(
                    CallReservation(
                        id = reservationId,
                        characterId = 2L,
                        firstName = "민준",
                        imageUrl = null,
                        scheduledAt = scheduledAt,
                    ),
                ),
            ),
        )

    private companion object {
        const val CALL_HISTORY_SIZE = 20
    }
}

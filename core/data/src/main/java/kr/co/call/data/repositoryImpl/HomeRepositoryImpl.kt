package kr.co.call.data.repositoryImpl

import java.time.LocalDateTime
import javax.inject.Inject
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.CallSender
import kr.co.call.domain.model.home.CallStatus
import kr.co.call.domain.model.home.CallType
import kr.co.call.domain.model.home.HomeModel
import kr.co.call.domain.repository.HomeRepository

class HomeRepositoryImpl @Inject constructor() : HomeRepository {

    override suspend fun getReservations(): Result<HomeModel.Reservations> =
        Result.success(
            HomeModel.Reservations(
                count = 1,
                items = listOf(
                    HomeModel.Reservation(
                        callReservationId = 3L,
                        characterId = 2L,
                        firstName = "민준",
                        imageUrl = null,
                        scheduledAt = LocalDateTime.of(2026, 6, 30, 21, 0),
                        status = HomeModel.ReservationStatus.SCHEDULED,
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
                    sender = if (index % 2 == 0) CallSender.USER else CallSender.AI,
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
                    callType = if (index % 2 == 0) CallType.DAILY else CallType.DEEP,
                    status = if (index % 4 == 3) CallStatus.MISSED else CallStatus.COMPLETED,
                )
            },
        )

    override suspend fun getSummary(characterId: Long): Result<HomeModel.Summary> =
        Result.success(
            HomeModel.Summary(
                characterId = characterId,
                firstName = "수현",
                relationshipDays = 30,
                totalCallCount = 24,
                callStreakDays = 12,
            ),
        )

    private companion object {
        const val CALL_HISTORY_SIZE = 20
    }
}

package kr.co.call.impl.screen

import androidx.paging.PagingData
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kr.co.call.domain.model.home.HomeModel
import kr.co.call.impl.mapper.toUiState
import kr.co.call.impl.mock.CallMockData
import kr.co.call.impl.viewmodel.NotificationType
import kr.co.call.impl.viewmodel.state.CallHistoryState
import kr.co.call.impl.viewmodel.state.HomeNotificationState
import kr.co.call.impl.viewmodel.state.HomeReservationState
import kr.co.call.impl.viewmodel.state.HomeSummaryState

/**
 * 홈화면에서 사용하는 Mock Data
 */
internal data class HomeMockData(
    val home: HomeModel,
    val hasUnreadNotification: Boolean,
    val notifications: List<HomeNotificationState>,
) {
    val summaryState: HomeSummaryState
        get() = HomeSummaryState(
            characterId = home.summary.characterId,
            firstName = home.summary.firstName,
            relationshipDays = home.summary.relationshipDays,
            totalCallCount = home.summary.totalCallCount,
            callStreakDays = home.summary.callStreakDays,
        )

    val reservationState: HomeReservationState
        get() {
            val reservation = home.reservations.items.firstOrNull()

            return HomeReservationState(
                reservationCount = home.reservations.count,
                profileImageUrl = reservation?.imageUrl,
                scheduledAt = reservation?.scheduledAt,
                firstName = reservation?.firstName,
            )
        }

    val callHistoryState: CallHistoryState
        get() = CallHistoryState(
            histories = home.callHistories.map { callHistory -> callHistory.toUiState() },
        )

    fun notificationFlow(): Flow<PagingData<HomeNotificationState>> =
        flowOf(PagingData.from(notifications))
}

internal fun createHomeMockData(
    now: LocalDateTime = LocalDateTime.now(),
): HomeMockData =
    HomeMockData(
        home = HomeModel(
            summary = HomeModel.Summary(
                characterId = 2L,
                firstName = "수현",
                relationshipDays = 30,
                totalCallCount = 24,
                callStreakDays = 12,
            ),
            reservations = HomeModel.Reservations(
                count = 2,
                items = listOf(
                    HomeModel.Reservation(
                        callReservationId = 3L,
                        characterId = 2L,
                        firstName = "민준",
                        imageUrl = null,
                        scheduledAt = now.withHour(21).withMinute(0).withSecond(0),
                        status = HomeModel.ReservationStatus.SCHEDULED,
                    ),
                ),
            ),
            callHistories = CallMockData.histories,
        ),
        hasUnreadNotification = true,
        notifications = listOf(
            HomeNotificationState(
                notificationId = 42L,
                type = NotificationType.CALL_RESERVATION,
                title = "통화 약속",
                content = "오늘 밤 9시 통화 15분 뒤에 시작됩니다.",
                isRead = false,
                createdAt = now.minusMinutes(1),
                characterName = "민준",
                profileImageUrl = "https://cdn.lovecall.com/presets/male/1.jpg",
            ),
            HomeNotificationState(
                notificationId = 41L,
                type = NotificationType.MISSED_CALL,
                title = "부재중 전화",
                content = "민준이에게서 받지 못한 전화가 있어요.",
                isRead = false,
                createdAt = now.minusMinutes(30),
                characterName = "민준",
                profileImageUrl = "https://cdn.lovecall.com/presets/male/1.jpg",
            ),
            HomeNotificationState(
                notificationId = 40L,
                type = NotificationType.ANNIVERSARY,
                title = "기념일",
                content = "오늘은 민준이와 함께한지 30일 째!\n작은 기념일을 함께 축하해요 💗",
                isRead = true,
                createdAt = now.minusHours(1),
                characterName = "민준",
                profileImageUrl = null,
            ),
        ),
    )

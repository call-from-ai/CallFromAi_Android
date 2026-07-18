package kr.co.call.impl.screen

import androidx.paging.PagingData
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.CallReservation
import kr.co.call.domain.model.home.CallReservations
import kr.co.call.domain.model.home.HomeNotification
import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.domain.model.home.NotificationType
import kr.co.call.impl.mapper.toUiModel
import kr.co.call.impl.mock.CallMockData
import kr.co.call.impl.viewmodel.model.CallHistoryUiModel
import kr.co.call.impl.viewmodel.model.HomeReservationUiModel
import kr.co.call.impl.viewmodel.model.HomeSummaryUiModel

/**
 * 홈화면에서 사용하는 Mock Data
 */
data class HomeMockData(
    val summary: HomeSummary,
    val reservations: CallReservations,
    val callHistories: List<CallHistory>,
    val hasUnreadNotification: Boolean,
    val notifications: List<HomeNotification>,
) {
    val summaryUiModel: HomeSummaryUiModel
        get() = summary.toUiModel()

    val reservationUiModel: HomeReservationUiModel
        get() = reservations.toUiModel()

    val callHistoryUiModels: List<CallHistoryUiModel>
        get() = callHistories.map { callHistory -> callHistory.toUiModel() }

    fun notificationFlow(): Flow<PagingData<HomeNotification>> =
        flowOf(PagingData.from(notifications))
}

fun createHomeMockData(
    now: LocalDateTime = LocalDateTime.now(),
): HomeMockData =
    HomeMockData(
        summary = HomeSummary(
            firstName = "수현",
            relationshipDays = 30,
            totalCallCount = 24,
            callStreakDays = 12,
        ),
        reservations = CallReservations(
            totalCount = 2,
            items = listOf(
                CallReservation(
                    id = 3L,
                    characterId = 2L,
                    firstName = "민준",
                    imageUrl = null,
                    scheduledAt = now.withHour(21).withMinute(0).withSecond(0),
                ),
            ),
        ),
        callHistories = CallMockData.histories,
        hasUnreadNotification = true,
        notifications = listOf(
            HomeNotification(
                notificationId = 42L,
                type = NotificationType.CALL_RESERVATION,
                title = "통화 약속",
                content = "오늘 밤 9시 통화 15분 뒤에 시작됩니다.",
                isRead = false,
                createdAt = now.minusMinutes(1),
                characterName = "민준",
                profileImageUrl = "https://cdn.lovecall.com/presets/male/1.jpg",
            ),
            HomeNotification(
                notificationId = 41L,
                type = NotificationType.MISSED_CALL,
                title = "부재중 전화",
                content = "민준이에게서 받지 못한 전화가 있어요.",
                isRead = false,
                createdAt = now.minusMinutes(30),
                characterName = "민준",
                profileImageUrl = "https://cdn.lovecall.com/presets/male/1.jpg",
            ),
            HomeNotification(
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

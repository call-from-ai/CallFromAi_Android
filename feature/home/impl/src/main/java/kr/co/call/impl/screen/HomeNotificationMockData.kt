package kr.co.call.impl.screen

import java.time.LocalDateTime
import kr.co.call.domain.model.home.HomeNotification
import kr.co.call.domain.model.home.NotificationType

internal fun createHomeNotificationMockData(
    now: LocalDateTime = LocalDateTime.now(),
): List<HomeNotification> =
    listOf(
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
    )

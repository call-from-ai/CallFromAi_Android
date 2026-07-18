package kr.co.call.impl.screen

import java.time.LocalDateTime
import kr.co.call.core.common.util.TimeUtil
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeNotificationTimeFormatterTest {

    private val now = LocalDateTime.of(2026, 7, 17, 15, 0)

    @Test
    fun `경과한 시간을 분 단위로 내림해 표시한다`() {
        val createdAt = now.minusMinutes(3).minusSeconds(59)

        val result = TimeUtil.toTimeAgoText(createdAt, now)

        assertEquals("3분 전", result)
    }

    @Test
    fun `1분 미만은 0분 전으로 표시한다`() {
        val createdAt = now.minusSeconds(59)

        val result = TimeUtil.toTimeAgoText(createdAt, now)

        assertEquals("0분 전", result)
    }

    @Test
    fun `미래 시각은 음수 대신 0분 전으로 표시한다`() {
        val createdAt = now.plusMinutes(1)

        val result = TimeUtil.toTimeAgoText(createdAt, now)

        assertEquals("0분 전", result)
    }

    @Test
    fun `60분 이상은 시간 단위로 내림해 표시한다`() {
        val createdAt = now.minusHours(3).minusMinutes(59)

        val result = TimeUtil.toTimeAgoText(createdAt, now)

        assertEquals("3시간 전", result)
    }

    @Test
    fun `24시간 이상은 일 단위로 내림해 표시한다`() {
        val createdAt = now.minusDays(2).minusHours(23)

        val result = TimeUtil.toTimeAgoText(createdAt, now)

        assertEquals("2일 전", result)
    }
}

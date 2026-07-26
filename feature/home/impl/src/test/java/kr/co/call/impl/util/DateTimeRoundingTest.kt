package kr.co.call.impl.util

import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class DateTimeRoundingTest {

    @Test
    fun `30분 단위로 내림한다`() {
        val dateTime = LocalDateTime.of(2026, 7, 23, 10, 17, 20)

        val result = dateTime.roundToMinutes(
            intervalMinutes = 30,
            direction = TimeRoundDirection.DOWN,
        )

        assertEquals(
            LocalDateTime.of(2026, 7, 23, 10, 0),
            result,
        )
    }

    @Test
    fun `30분 단위로 올림한다`() {
        val dateTime = LocalDateTime.of(2026, 7, 23, 10, 17, 20)

        val result = dateTime.roundToMinutes(
            intervalMinutes = 30,
            direction = TimeRoundDirection.UP,
        )

        assertEquals(
            LocalDateTime.of(2026, 7, 23, 10, 30),
            result,
        )
    }

    @Test
    fun `이미 경계 시각이면 올림해도 유지한다`() {
        val dateTime = LocalDateTime.of(2026, 7, 23, 10, 30)

        val result = dateTime.roundToMinutes(
            intervalMinutes = 30,
            direction = TimeRoundDirection.UP,
        )

        assertEquals(dateTime, result)
    }
}

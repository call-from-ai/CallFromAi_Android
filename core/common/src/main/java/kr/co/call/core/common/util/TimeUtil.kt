package kr.co.call.core.common.util

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtil {
    fun parseLocalDateTime(value: String): LocalDateTime =
        LocalDateTime.parse(value)

    fun toCallHistoryDateText(dateTime: LocalDateTime): String =
        dateTime.format(
            DateTimeFormatter.ofPattern(
                "M월 d일 a h시 m분",
                Locale.KOREAN,
            ),
        )

    fun toTimeAgoText(
        dateTime: LocalDateTime,
        now: LocalDateTime = LocalDateTime.now(),
    ): String {
        val elapsedMinutes = Duration.between(dateTime, now)
            .toMinutes()
            .coerceAtLeast(0)

        return when {
            elapsedMinutes < 60 -> "${elapsedMinutes}분 전"
            elapsedMinutes < 60 * 24 -> "${elapsedMinutes / 60}시간 전"
            else -> "${elapsedMinutes / (60 * 24)}일 전"
        }
    }

    fun toHomeDateText(date: LocalDate): String =
        date.format(
            DateTimeFormatter.ofPattern(
                "M월 d일 EEEE",
                Locale.KOREAN,
            ),
        )

    fun toReservationTimeText(
        dateTime: LocalDateTime,
        today: LocalDate = LocalDate.now(),
    ): String {
        val dateText = when (dateTime.toLocalDate()) {
            today -> "오늘"
            today.plusDays(1) -> "내일"
            else -> dateTime.format(DateTimeFormatter.ofPattern("M월 d일"))
        }
        val periodText = when (dateTime.hour) {
            in 0..5 -> "새벽"
            in 6..11 -> "아침"
            in 12..17 -> "낮"
            in 18..20 -> "저녁"
            else -> "밤"
        }
        val hourText = when (val hourOf12 = dateTime.hour % 12) {
            0 -> 12
            else -> hourOf12
        }
        val minuteText = if (dateTime.minute == 0) {
            ""
        } else {
            " ${dateTime.minute}분"
        }

        return "$dateText $periodText ${hourText}시$minuteText"
    }

    fun toHourMinuteText(dateTime: LocalDateTime): String =
        dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
}

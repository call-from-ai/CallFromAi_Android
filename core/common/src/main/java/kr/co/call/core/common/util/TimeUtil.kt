package kr.co.call.core.common.util

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 날짜 및 시간과 관련된 변환 및 포맷팅 처리를 담당하는 유틸리티 객체입니다.
 *
 * 주로 한국어 로케일을 기반으로 통화 내역, 홈 화면, 예약 시간 등에 사용되는
 * 다양한 형식의 문자열 변환 기능을 제공합니다.
 */
object TimeUtil {

    // LocalDateTime을 String으로 변환
    fun parseLocalDateTime(value: String): LocalDateTime =
        LocalDateTime.parse(value)

    // LocalDate를 String으로 변환
    fun toCallHistoryDateText(dateTime: LocalDateTime): String =
        dateTime.format(
            DateTimeFormatter.ofPattern(
                "M월 d일 a h시 m분",
                Locale.KOREAN,
            ),
        )

    // LocalDateTime을 "n분 전" 형식의 문자열로 변환
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

    // LocalDate를 "M월 d일 EEEE" 형식의 문자열로 변환
    fun toHomeDateText(date: LocalDate): String =
        date.format(
            DateTimeFormatter.ofPattern(
                "M월 d일 EEEE",
                Locale.KOREAN,
            ),
        )

    // LocalDateTime을 "오늘/내일 + 시간" 형식의 문자열로 변환
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

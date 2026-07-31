package kr.co.call.core.common.util

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 날짜 및 시간과 관련된 변환 및 포맷팅 처리를 담당하는 유틸리티 객체입니다.
 *
 * 주로 한국어 로케일을 기반으로 통화 내역과 홈 화면에 사용되는
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

}

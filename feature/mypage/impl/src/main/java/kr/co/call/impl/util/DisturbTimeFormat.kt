package kr.co.call.impl.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * API LocalTime 문자열 파싱.
 * 지원 예: "23:30:00", "23:30", "23:30:00.000"
 */
fun parseApiLocalTime(value: String?): LocalTime? {
    if (value.isNullOrBlank()) return null
    val normalized = value.trim()
        .removeSuffix("Z")
        .let { raw ->
            // "23:30:00.000" → "23:30:00"
            val dot = raw.indexOf('.')
            if (dot >= 0) raw.substring(0, dot) else raw
        }

    val patterns = listOf(
        DateTimeFormatter.ISO_LOCAL_TIME,
        DateTimeFormatter.ofPattern("H:mm:ss"),
        DateTimeFormatter.ofPattern("H:mm"),
        DateTimeFormatter.ofPattern("HH:mm:ss"),
        DateTimeFormatter.ofPattern("HH:mm"),
    )
    for (formatter in patterns) {
        try {
            return LocalTime.parse(normalized, formatter)
        } catch (_: DateTimeParseException) {
            // try next
        }
    }
    return null
}

/** LocalTime → API "HH:mm:ss" */
fun LocalTime.toApiTimeString(): String =
    format(DateTimeFormatter.ofPattern("HH:mm:ss"))

/**
 * 단일 시각 표시 (상세 화면 필드와 동일).
 * 09:00 → "09시", 09:30 → "09시 30분"
 */
fun formatDisturbTimeLabel(time: LocalTime): String {
    return if (time.minute == 0) {
        String.format(Locale.getDefault(), "%02d시", time.hour)
    } else {
        String.format(Locale.getDefault(), "%02d시 %02d분", time.hour, time.minute)
    }
}

/**
 * 프로필 trailing 범위 표시.
 * - 둘 다 정각: "09-12시" (기존 디자인)
 * - 분 포함: "23시 30분-01시" / "23시 30분-01시 30분"
 */
fun formatDisturbRangeText(start: LocalTime?, end: LocalTime?): String {
    if (start == null || end == null) return ""
    return if (start.minute == 0 && end.minute == 0) {
        String.format(Locale.getDefault(), "%02d-%02d시", start.hour, end.hour)
    } else {
        "${formatDisturbTimeLabel(start)}-${formatDisturbTimeLabel(end)}"
    }
}

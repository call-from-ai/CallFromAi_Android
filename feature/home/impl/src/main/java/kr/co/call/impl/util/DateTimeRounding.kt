package kr.co.call.impl.util

import java.time.LocalDateTime

/**
 * 시간을 분 단위로 반올림하는 Util
 */
fun LocalDateTime.roundToMinutes(
    intervalMinutes: Int,
    direction: TimeRoundDirection,
): LocalDateTime {
    require(intervalMinutes > 0) {
        "시간 간격은 0보다 커야 합니다."
    }

    val minuteBase = withSecond(0).withNano(0)
    val minuteOfDay = minuteBase.hour * 60 + minuteBase.minute
    val remainder = minuteOfDay % intervalMinutes

    return when (direction) {
        TimeRoundDirection.DOWN -> {
            minuteBase.minusMinutes(remainder.toLong())
        }

        TimeRoundDirection.UP -> {
            val hasSubMinute = second > 0 || nano > 0
            val minutesToAdd = when (remainder) {
                0 if !hasSubMinute -> 0
                0 -> intervalMinutes
                else -> intervalMinutes - remainder
            }

            minuteBase.plusMinutes(minutesToAdd.toLong())
        }
    }
}

enum class TimeRoundDirection {
    UP,
    DOWN,
}


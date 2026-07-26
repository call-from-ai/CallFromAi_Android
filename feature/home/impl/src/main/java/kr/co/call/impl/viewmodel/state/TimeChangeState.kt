package kr.co.call.impl.viewmodel.state

import java.time.LocalDate
import java.time.LocalTime

/**
 * 시간 변경 바텀시트 상태
 */
data class TimeChangeState(
    val selectedDate: LocalDate,
    val selectedTime: LocalTime,
)

package kr.co.call.impl.viewmodel

import java.time.LocalTime

enum class DisturbTimeSheetType {
    Start,
    End,
}

// api 연동전 임시로만!
data class DisturbTimeState(
    val startTime: LocalTime = DEFAULT_START,
    val endTime: LocalTime = DEFAULT_END,
    val openSheet: DisturbTimeSheetType? = null,
    val draftTime: LocalTime = DEFAULT_START,
) {
    val canComplete: Boolean
        get() = startTime != endTime

    companion object {
        val DEFAULT_START: LocalTime = LocalTime.of(9, 0)
        val DEFAULT_END: LocalTime = LocalTime.of(12, 30)
    }
}

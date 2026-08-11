package kr.co.call.impl.viewmodel

import java.time.LocalTime
import kr.co.call.domain.util.LoadStatus

enum class DisturbTimeSheetType {
    Start,
    End,
}

data class DisturbTimeState(
    val startTime: LocalTime = DEFAULT_START,
    val endTime: LocalTime = DEFAULT_END,
    val openSheet: DisturbTimeSheetType? = null,
    val draftTime: LocalTime = DEFAULT_START,
    val loadStatus: LoadStatus = LoadStatus.Idle,
    val isSaving: Boolean = false,
    val isClearedPending: Boolean = false,
) {
    val canComplete: Boolean
        get() = !isSaving &&
            loadStatus !is LoadStatus.Loading &&
            (startTime != endTime || isClearedPending)

    companion object {
        val DEFAULT_START: LocalTime = LocalTime.of(9, 0)
        val DEFAULT_END: LocalTime = LocalTime.of(12, 30)
        val CLEARED_TIME: LocalTime = LocalTime.MIDNIGHT
    }
}

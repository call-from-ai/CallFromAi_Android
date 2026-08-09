package kr.co.call.impl.viewmodel

import java.time.LocalTime

sealed interface DisturbTimeIntent {
    data object ClickStartTime : DisturbTimeIntent
    data object ClickEndTime : DisturbTimeIntent
    data class SelectDraftTime(val time: LocalTime) : DisturbTimeIntent
    data object ConfirmSheet : DisturbTimeIntent
    data object DismissSheet : DisturbTimeIntent
    data object ClickComplete : DisturbTimeIntent
    data object ClickDelete : DisturbTimeIntent
}

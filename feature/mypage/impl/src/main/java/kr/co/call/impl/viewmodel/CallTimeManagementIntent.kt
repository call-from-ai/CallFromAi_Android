package kr.co.call.impl.viewmodel

import kr.co.call.impl.component.PreferTime

sealed interface CallTimeManagementIntent {
    data class SelectPreferTime(val preferTime: PreferTime) : CallTimeManagementIntent
    data object ClickComplete : CallTimeManagementIntent
    data object ClickBack : CallTimeManagementIntent
}

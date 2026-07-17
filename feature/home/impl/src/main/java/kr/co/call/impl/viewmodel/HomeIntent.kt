package kr.co.call.impl.viewmodel

import kr.co.call.impl.tab.HomeHistoryTab

internal sealed interface HomeIntent {
    data class SelectHistoryTab(
        val tab: HomeHistoryTab,
    ) : HomeIntent

    data object ClickCall : HomeIntent

    data object ClickNotification : HomeIntent

    data object ClickChangeTime : HomeIntent
}
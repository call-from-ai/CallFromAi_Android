package kr.co.call.impl.viewmodel.state

import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.tab.HomeHistoryTab
import kr.co.call.impl.viewmodel.state.HomeReservationState
import kr.co.call.impl.viewmodel.state.HomeSummaryState

data class HomeState(
    val summary: HomeSummaryState = HomeSummaryState(),
    val reservation: HomeReservationState = HomeReservationState(),
    val callHistory: CallHistoryState = CallHistoryState(),
    val hasUnreadNotification: Boolean = true,
    val loadStatus: LoadStatus = LoadStatus.Idle,
    val selectedHistoryTab: HomeHistoryTab = HomeHistoryTab.NOTIFICATION,
) {
    val isLoading: Boolean
        get() = loadStatus == LoadStatus.Loading
}
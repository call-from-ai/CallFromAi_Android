package kr.co.call.impl.viewmodel.state

import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.tab.HomeHistoryTab
import kr.co.call.impl.viewmodel.model.CallHistoryUiModel
import kr.co.call.impl.viewmodel.model.HomeReservationUiModel
import kr.co.call.impl.viewmodel.model.HomeSummaryUiModel

data class HomeState(
    val characterId: Long = 0L,
    val summary: HomeSummaryUiModel = HomeSummaryUiModel(),
    val reservation: HomeReservationUiModel = HomeReservationUiModel(),
    val callHistories: List<CallHistoryUiModel> = emptyList(),
    val hasUnreadNotification: Boolean = true,
    val loadStatus: LoadStatus = LoadStatus.Idle,
    val selectedHistoryTab: HomeHistoryTab = HomeHistoryTab.NOTIFICATION,
) {
    val isLoading: Boolean
        get() = loadStatus == LoadStatus.Loading
}

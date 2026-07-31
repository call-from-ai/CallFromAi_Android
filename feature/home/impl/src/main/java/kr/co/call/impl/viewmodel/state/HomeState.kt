package kr.co.call.impl.viewmodel.state

import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.HomeCharacter
import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.tab.HomeHistoryTab

data class HomeState(
    val summary: HomeSummary? = null,
    val callHistories: List<CallHistory> = emptyList(),
    val hasUnreadNotification: Boolean = true,
    val loadStatus: LoadStatus = LoadStatus.Idle,
    val selectedHistoryTab: HomeHistoryTab = HomeHistoryTab.NOTIFICATION,
    val characters: List<HomeCharacter> = emptyList(),
    val dialogState: HomeDialogState? = null,
)

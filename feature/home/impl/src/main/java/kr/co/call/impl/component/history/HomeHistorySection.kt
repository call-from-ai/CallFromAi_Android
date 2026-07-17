package kr.co.call.impl.component.history

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kr.co.call.impl.tab.HomeHistoryTab
import kr.co.call.impl.tab.HomeHistoryTabBar
import kr.co.call.impl.viewmodel.model.CallHistoryUiModel
import kr.co.call.impl.viewmodel.state.HomeNotificationState

@Composable
internal fun HomeHistorySection(
    selectedTab: HomeHistoryTab,
    onTabClick: (HomeHistoryTab) -> Unit,
    notifications: Flow<PagingData<HomeNotificationState>>,
    callHistories: List<CallHistoryUiModel>,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = Modifier.height(22.dp))
    HomeHistoryTabBar(
        selectedTab = selectedTab,
        onTabClick = onTabClick,
        modifier = modifier,
    )

    // 탭 선택 상태에 따른 분기
    when (selectedTab) {
        HomeHistoryTab.NOTIFICATION -> {
            NotificationList(notifications = notifications)
        }

        HomeHistoryTab.CALL_HISTORY -> {
            CallHistoryList(histories = callHistories)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeHistorySectionPreview(){
    HomeHistoryTabBar(
        selectedTab = HomeHistoryTab.NOTIFICATION,
        onTabClick = {},
    )
}

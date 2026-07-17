package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.component.header.HomeHeader
import kr.co.call.impl.component.history.HomeHistorySection
import kr.co.call.impl.component.header.HomeReservationCard
import kr.co.call.impl.tab.HomeHistoryTab
import kr.co.call.impl.viewmodel.state.CallHistoryState
import kr.co.call.impl.viewmodel.state.HomeSummaryState
import kr.co.call.impl.viewmodel.HomeIntent
import kr.co.call.impl.viewmodel.state.HomeNotificationState
import kr.co.call.impl.viewmodel.state.HomeReservationState
import kr.co.call.impl.viewmodel.HomeViewModel
import androidx.paging.PagingData
import org.orbitmvi.orbit.compose.collectAsState

/**
 * 홈 화면 진입 컴포넌트
 * - ViewModel 상태 구독
 * - 헤더와 약속 상태를 화면 콘텐츠에 전달
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val notifications = remember { createHomeMockData().notificationFlow() }

    HomeScreenContent(
        homeSummaryState = state.summary,
        hasUnreadNotification = state.hasUnreadNotification,
        homeReservationState = state.reservation,
        callHistoryState = state.callHistory,
        selectedHistoryTab = state.selectedHistoryTab,
        notifications = notifications,
        onHistoryTabClick = { tab ->
            viewModel.handleIntent(HomeIntent.SelectHistoryTab(tab))
        },
        modifier = modifier,
    )
}

/**
 * 홈 화면 콘텐츠 컴포넌트
 * - 홈 헤더와 약속 배너 배치
 * - 배경과 영역 구분선 표시
 */
@Composable
internal fun HomeScreenContent(
    homeSummaryState: HomeSummaryState,
    hasUnreadNotification: Boolean,
    homeReservationState: HomeReservationState,
    callHistoryState: CallHistoryState,
    selectedHistoryTab: HomeHistoryTab,
    notifications: Flow<PagingData<HomeNotificationState>>,
    onHistoryTabClick: (HomeHistoryTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.background),
    ) {
        HomeHeader(
            summaryState = homeSummaryState,
            hasUnreadNotification = hasUnreadNotification,
        )

        // 예약된 전화 조회
        HomeReservationCard(reservationState = homeReservationState)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .background(CallTheme.colors.gray100),
        )

        // 지난 알림, 통화 기록 표시 섹션
        HomeHistorySection(
            selectedTab = selectedHistoryTab,
            onTabClick = onHistoryTabClick,
            notifications = notifications,
            callHistoryState = callHistoryState,
        )
    }
}

/**
 * 홈 화면 Preview
 */
@Preview(showBackground = true, widthDp = 412)
@Composable
private fun HomeScreenContentPreview() {
    val mockData = remember {
        createHomeMockData(
            now = LocalDateTime.of(2026, 7, 17, 15, 0),
        )
    }
    var selectedHistoryTab by remember {
        mutableStateOf(HomeHistoryTab.CALL_HISTORY)
    }

    CallFromAiTheme {
        HomeScreenContent(
            homeSummaryState = mockData.summaryState,
            hasUnreadNotification = mockData.hasUnreadNotification,
            homeReservationState = mockData.reservationState,
            callHistoryState = mockData.callHistoryState,
            selectedHistoryTab = selectedHistoryTab,
            notifications = mockData.notificationFlow(),
            onHistoryTabClick = { selectedHistoryTab = it },
        )
    }
}

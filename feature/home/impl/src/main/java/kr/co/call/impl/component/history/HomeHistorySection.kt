package kr.co.call.impl.component.history

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.impl.tab.HomeHistoryTab
import kr.co.call.impl.tab.HomeHistoryTabBar

@Composable
fun HomeHistorySection(
    selectedTab: HomeHistoryTab,
    onTabClick: (HomeHistoryTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = Modifier.height(22.dp))
    HomeHistoryTabBar(
        selectedTab = selectedTab,
        onTabClick = onTabClick,
        modifier = modifier,
    )

}

@Preview(showBackground = true)
@Composable
private fun HomeHistorySectionPreview(){
    HomeHistoryTabBar(
        selectedTab = HomeHistoryTab.NOTIFICATION,
        onTabClick = {},
    )
}

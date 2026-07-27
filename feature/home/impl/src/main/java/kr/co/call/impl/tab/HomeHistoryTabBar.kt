package kr.co.call.impl.tab

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun HomeHistoryTabBar(
    selectedTab: HomeHistoryTab,
    onTabClick: (HomeHistoryTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = HomeHistoryTab.entries
    val selectedTabIndex = tabs.indexOf(selectedTab)

    PrimaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.fillMaxWidth(),
        edgePadding = 0.dp,
        minTabWidth = 80.dp,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    selectedTabIndex = selectedTabIndex,
                    matchContentSize = true,
                ),
                width = Dp.Unspecified,
                height = 2.dp,
                color = CallTheme.colors.mainVariant1,
            )
        },
        divider = {
            HorizontalDivider(
                thickness = 1.dp,
                color = CallTheme.colors.gray100,
            )
        },
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onTabClick(tab) },
                text = {
                    Text(
                        text = tab.title,
                        style = CallTheme.typography.bodyMediumMedium,
                    )
                },
                selectedContentColor = CallTheme.colors.mainVariant1,
                unselectedContentColor = CallTheme.colors.gray800,

            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun HomeHistoryTabBarPreview() {
    var selectedTab by remember { mutableStateOf(HomeHistoryTab.NOTIFICATION) }

    CallFromAiTheme {
        HomeHistoryTabBar(
            selectedTab = selectedTab,
            onTabClick = { selectedTab = it },
        )
    }
}

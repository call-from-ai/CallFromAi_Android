package kr.co.call.impl.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.modifier.noRippleClickable
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
            val isSelected = tab == selectedTab

            Box(
                modifier = Modifier
                    .widthIn(min = 80.dp)
                    .height(48.dp)
                    .noRippleClickable(
                        onClickLabel = "${tab.title} 탭 선택",
                        role = Role.Tab,
                        onClick = { onTabClick(tab) },
                    )
                    .semantics {
                        selected = isSelected
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = tab.title,
                    color = if (isSelected) {
                        CallTheme.colors.mainVariant1
                    } else {
                        CallTheme.colors.gray800
                    },
                    style = CallTheme.typography.bodyMediumMedium,
                )
            }
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

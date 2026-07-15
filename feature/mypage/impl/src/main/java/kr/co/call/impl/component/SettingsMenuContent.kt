package kr.co.call.impl.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 메뉴 내부 아이템
 *
 * @param trailing 우측 영역 자유롭게 커스터마이즈 (기본값 : '>')
 */

@Composable
fun SettingsMenuContent(
    icon: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit = {
        Icon(
            painter = painterResource(id = R.drawable.ic_mypage_arrow_right),
            contentDescription = "바로가기",
        )
    },
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp, horizontal = 25.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 메뉴 아이콘
        Text(
            text = icon,
        )
        Spacer(modifier = Modifier.width(16.dp))
        // 메뉴 이름
        Text(
            text = label,
            style = CallTheme.typography.bodyMediumMedium,
            color = CallTheme.colors.black,
            modifier = Modifier.weight(1f),
        )
        // 메뉴 우측 요소
        trailing()
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsMenuItemPreview() {
    CallFromAiTheme {
        SettingsMenuContent(
            icon = "\uD83D\uDC64",
            label = "캐릭터 관리",
            onClick = {},
        )
    }
}
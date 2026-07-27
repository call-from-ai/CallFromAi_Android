package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 메뉴 섹션 묶음
 *
 *  @param title title이 없으면(단일 항목) 제목 없이 카드만 렌더링
 */

@Composable
fun SettingsSectionCard(
    modifier: Modifier = Modifier,
    title: String = "",
    items: List<@Composable () -> Unit>,
) {
    val cardShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(CallTheme.colors.white)
            .border(width = 1.dp, color = CallTheme.colors.gray100, shape =cardShape )
    ) {
        // 메뉴 제목
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = CallTheme.typography.bodySmallBold,
                color = CallTheme.colors.gray800,
                modifier = Modifier.padding(start = 25.dp, top = 15.dp, bottom = 7.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 내부 요소
        items.forEachIndexed { index, itemContent ->
            itemContent()

            // 구분선 (마지막 아이템 제외)
            if (index < items.lastIndex) {
                HorizontalDivider(
                    color = CallTheme.colors.gray100,
                    thickness = 1.dp
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsSectionCardPreview1() {
    CallFromAiTheme {
        SettingsSectionCard(items = listOf(
            {SettingsMenuContent(icon = "\uD83D\uDC64",label = "캐릭터 관리",onClick = {},
            )}
        ))
    }
}

@Preview
@Composable
private fun SettingsSectionCardPreview2() {
    CallFromAiTheme {
        SettingsSectionCard(
            title = "고객 지원",
            items = listOf(
                { SettingsMenuContent(icon = "❓", label = "자주 하는 질문", onClick = {}) },
                { SettingsMenuContent(icon = "✉️", label = "문의하기", onClick = {}) },
                { SettingsMenuContent(icon = "📄", label = "약관", onClick = {}) },
                { SettingsMenuContent(icon = "ℹ️", label = "버전 정보", onClick = {},
                    trailing = {Text("v1.0.0", style = CallTheme.typography.bodySmall,color= CallTheme.colors.gray400)}) }
            )
        )
    }
}

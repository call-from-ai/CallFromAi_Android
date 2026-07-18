package kr.co.call.impl.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
 * 뒤로가기 버튼(<) + 타이틀 상단바
 */
@Composable
fun CommonTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    onBackClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 뒤로가기 버튼(<)
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mypage_arrow_left),
                contentDescription = "뒤로가기",
            )
        }

        // 상단 타이틀
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = CallTheme.typography.bodyLargeBold,
                color = CallTheme.colors.black,
                modifier = Modifier.weight(1f),
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommonTopBarPreview() {
    CallFromAiTheme {
        CommonTopAppBar(
            title = "캐릭터 관리",
            onBackClick = {}
        )
    }
}
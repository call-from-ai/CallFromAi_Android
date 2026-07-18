package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kr.co.call.designsystem.R
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.component.CommonTopAppBar

@Composable
fun ComingSoonScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onBackClick,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CallTheme.colors.background),
        ) {
            // 상단 앱 바
            CommonTopAppBar(onBackClick = onBackClick)

            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // 로고 아이콘
                    Icon(
                        /**TODO: 실제 앱 로고 아이콘으로 교체 */
                        painter = painterResource(id = R.drawable.ic_mypage_ticket),
                        modifier = Modifier.size(81.dp),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // 텍스트
                    Text(
                        text = "준비 중입니다...",
                        style = CallTheme.typography.bodyMediumBold,
                        fontSize = 34.sp,
                        color = CallTheme.colors.mainVariant1,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ComingSoonScreenPreview(){
    CallFromAiTheme {
        ComingSoonScreen(
            onBackClick = {}
        )
    }
}
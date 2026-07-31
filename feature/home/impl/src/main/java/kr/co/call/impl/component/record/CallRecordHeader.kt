package kr.co.call.impl.component.record

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.modifier.noRippleClickable
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.CallInfo
import java.time.LocalDateTime

/**
 * 통화 기록 화면의 뒤로가기와 제목을 표시하는 상단 영역
 */
@Composable
fun CallRecordTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(52.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .noRippleClickable(
                    onClickLabel = "뒤로가기",
                    role = Role.Button,
                    onClick = onBackClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_mypage_arrow_left),
                contentDescription = "뒤로가기",
                modifier = Modifier.size(width = 10.dp, height = 18.dp),
            )
        }

        Text(
            text = "기록",
            color = CallTheme.colors.black,
            style = CallTheme.typography.bodyLargeBold,
        )
    }
}

/**
 * 조회한 통화 기록의 제목과 통화 정보를 표시하는 영역
 */
@Composable
fun CallRecordSummary(
    record: CallInfo,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(21.dp))
        Text(
            text = record.title,
            color = CallTheme.colors.black,
            style = CallTheme.typography.titleSmallBold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = TimeUtil.toCallHistoryDateText(record.calledAt),
            color = CallTheme.colors.gray600,
            style = CallTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "${record.characterName}님과의 통화",
            color = CallTheme.colors.gray600,
            style = CallTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(
            color = CallTheme.colors.gray100,
        )
    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun CallRecordTopBarPreview() {
    CallFromAiTheme {
        CallRecordTopBar(
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun CallRecordSummaryPreview() {
    CallFromAiTheme {
        CallRecordSummary(
            record = CallInfo(
                callId = 1L,
                title = "출근 준비와 아침 일정 이야기",
                calledAt = LocalDateTime.of(2026, 6, 27, 7, 31),
                characterName = "민준",
                recordingUrl = null,
                durationMillis = 0L,
            ),
        )
    }
}

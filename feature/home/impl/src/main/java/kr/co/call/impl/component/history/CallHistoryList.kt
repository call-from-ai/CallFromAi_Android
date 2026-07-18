package kr.co.call.impl.component.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.mock.CallMockData
import kr.co.call.impl.viewmodel.model.CallHistoryIconType
import kr.co.call.impl.viewmodel.model.CallHistoryUiModel

/**
 * 통화 기록 목록을 표시하는 컴포넌트
 * - 최근 통화 기록 최대 20건 표시
 * - 완료 여부에 따라 AI 요약과 기록 아이콘 노출
 */
@Composable
internal fun CallHistoryList(
    histories: List<CallHistoryUiModel>,
    modifier: Modifier = Modifier,
    onRecordClick: (Long) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "최근 20건의 기록만 표시됩니다.",
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                bottom = 18.dp,
            ),
            color = CallTheme.colors.gray800,
            style = CallTheme.typography.bodySmall,
        )

        histories.forEach { history ->
            CallHistoryCard(
                callHistory = history,
                onRecordClick = { onRecordClick(history.callId) },
            )
        }
    }
}

/**
 * 통화 기록 한 건을 표시하는 컴포넌트
 * - 부재중 통화는 이름과 시간만 표시
 * - 완료 통화는 AI 요약과 기록 이동 아이콘 표시
 */
@Composable
private fun CallHistoryCard(
    callHistory: CallHistoryUiModel,
    modifier: Modifier = Modifier,
    onRecordClick: () -> Unit = {},
) {
    // Missed type
    val isMissed = callHistory.iconType == CallHistoryIconType.MISSED

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isMissed) 70.dp else 90.dp),
    ) {
        CallHistoryIcon(
            iconType = callHistory.iconType,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 28.dp,
                    top = if (isMissed) 11.dp else 12.dp,
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 68.dp,
                    top = if (isMissed) 11.dp else 10.dp,
                    end = if (isMissed) 16.dp else 74.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = callHistory.characterName,
                color = CallTheme.colors.black,
                style = CallTheme.typography.bodySmallBold,
            )

            if (!isMissed) {
                Text(
                    text = callHistory.aiSummary,
                    color = CallTheme.colors.gray800,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = CallTheme.typography.bodySmall,
                )
            }

            Text(
                text = callHistory.startedAtText,
                color = CallTheme.colors.gray600,
                style = CallTheme.typography.caption,
            )
        }

        // 부재중 통화가 아닐 경우 기록 버튼 표시
        if (!isMissed) {
            Image(
                painter = painterResource(
                    id = kr.co.call.designsystem.R.drawable.ic_home_record,
                ),
                contentDescription = "통화 기록 보기",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 32.dp)
                    .size(42.dp)
                    .clickable(onClick = onRecordClick),
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .width(380.dp),
            thickness = 1.dp,
            color = CallTheme.colors.gray100,
        )
    }
}

// 아이콘 타입에 따라서 표시
@Composable
private fun CallHistoryIcon(
    iconType: CallHistoryIconType,
    modifier: Modifier = Modifier,
) {
    val iconResource = when (iconType) {
        CallHistoryIconType.SENT ->
            kr.co.call.designsystem.R.drawable.img_home_call_send

        CallHistoryIconType.RECEIVED ->
            kr.co.call.designsystem.R.drawable.img_home_call_received

        CallHistoryIconType.MISSED ->
            kr.co.call.designsystem.R.drawable.img_home_call_missed
    }
    val iconSize = when (iconType) {
        CallHistoryIconType.SENT -> DpSize(26.dp, 23.dp)
        CallHistoryIconType.RECEIVED -> DpSize(23.dp, 23.dp)
        CallHistoryIconType.MISSED -> DpSize(25.dp, 26.dp)
    }

    Image(
        painter = painterResource(id = iconResource),
        contentDescription = null,
        modifier = modifier.size(iconSize),
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 390)
@Composable
private fun CallHistoryListPreview() {
    CallFromAiTheme {
        CallHistoryList(
            histories = CallMockData.uiModels,
        )
    }
}

package kr.co.call.impl.component.record

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
import kr.co.call.designsystem.modifier.noRippleClickable
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 녹음 진행 상태와 5초 이동, 재생 동작을 제공하는 고정 하단 영역
 */
@Composable
fun CallRecordPlayer(
    state: CallRecordPlayerState,
    onSeek: (Long) -> Unit,
    onRewindClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onForwardClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // state에서 실제 재생 위치를 가져옴
    val durationMillis = state.durationMillis
    // 진행 비율 재계산
    val progress = if (durationMillis > 0L) {
        state.currentPositionMillis.toFloat() / durationMillis
    } else {
        0f
    }.coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(144.dp)
            .padding(horizontal = 16.dp),
    ) {
        CallRecordProgressBar(
            progress = progress,
            enabled = durationMillis > 0L,
            onProgressChange = { changedProgress ->
                onSeek((durationMillis * changedProgress).toLong())
            },
        )
        Spacer(modifier = Modifier.height(3.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            PlayerTimeText(
                text = state.currentPositionMillis.toPlayerTime(),
                isCurrent = true,
            )
            PlayerTimeText(
                text = state.durationMillis.toPlayerTime(),
                isCurrent = false,
            )
        }
        Spacer(modifier = Modifier.height(13.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = 42.dp,
                alignment = Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayerSeekButton(
                drawableRes = R.drawable.ic_home_call_replay_5,
                contentDescription = "5초 전",
                onClick = onRewindClick,
            )
            PlayPauseButton(
                isPlaying = state.isPlaying,
                onClick = onPlayPauseClick,
            )
            PlayerSeekButton(
                drawableRes = R.drawable.ic_home_call_replay_5_after,
                contentDescription = "5초 후",
                onClick = onForwardClick,
            )
        }
    }
}

/**
 * 녹음 진행 상태를 표시하는 Progress Bar
 */
@Composable
private fun CallRecordProgressBar(
    progress: Float,
    enabled: Boolean,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val inactiveColor = CallTheme.colors.gray100
    val activeColor = CallTheme.colors.mainVariant1
    val latestOnProgressChange = rememberUpdatedState(onProgressChange)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures { offset ->
                        if (size.width <= 0) return@detectTapGestures

                        val changedProgress =
                            (offset.x / size.width).coerceIn(0f, 1f)
                        latestOnProgressChange.value(changedProgress)
                    }
                }
            },
    ) {
        // 비율에 맞춰서 선의 끝 위치 그리기
        val centerY = size.height / 2f
        val progressX = size.width * progress

        drawLine(
            color = inactiveColor,
            start = Offset(0f, centerY),
            end = Offset(size.width, centerY),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = activeColor,
            start = Offset(0f, centerY),
            end = Offset(progressX, centerY),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = activeColor,
            radius = 4.dp.toPx(),
            center = Offset(progressX, centerY),
        )
    }
}

/**
 * 재생 시간 텍스트
 */
@Composable
private fun PlayerTimeText(
    text: String,
    isCurrent: Boolean,
) {
    Text(
        text = text,
        color = if (isCurrent) {
            CallTheme.colors.mainVariant1
        } else {
            CallTheme.colors.gray400
        },
        style = CallTheme.typography.captionBold,
    )
}

/**
 * 녹음 파일을 5초 이전 또는 이후로 이동하는 버튼
 */
@Composable
private fun PlayerSeekButton(
    drawableRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .noRippleClickable(
                onClickLabel = contentDescription,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
        )
    }
}

/**
 * 현재 재생 상태에 따라 재생 또는 일시정지 동작을 제공하는 버튼
 */
@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentDescription = if (isPlaying) "일시정지" else "재생"

    Box(
        modifier = modifier
            .size(48.dp)
            .noRippleClickable(
                onClickLabel = contentDescription,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(
                if (isPlaying) {
                    R.drawable.ic_home_call_pause
                } else {
                    R.drawable.ic_home_call_play
                },
            ),
            contentDescription = contentDescription,
            modifier = Modifier.size(34.dp),
        )
    }
}

// String -> 재생 시간으로 변환하는 확장함수
private fun Long.toPlayerTime(): String {
    val totalSeconds = (this / 1_000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%02d:%02d".format(minutes, seconds)
}


@Preview(showBackground = true, widthDp = 412)
@Composable
private fun CallRecordPlayerPreview() {
    CallFromAiTheme {
        CallRecordPlayer(
            state = CallRecordPlayerState(
                currentPositionMillis = 31_000L,
                durationMillis = 81_000L,
            ),
            onSeek = {},
            onRewindClick = {},
            onPlayPauseClick = {},
            onForwardClick = {},
        )
    }
}

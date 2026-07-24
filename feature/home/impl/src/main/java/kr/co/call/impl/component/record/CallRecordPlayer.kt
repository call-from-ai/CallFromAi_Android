package kr.co.call.impl.component.record

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.R
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
    val progress = if (state.durationMillis > 0L) {
        state.currentPositionMillis.toFloat() / state.durationMillis
    } else {
        0f
    }.coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(108.dp)
            .padding(horizontal = 16.dp),
    ) {
        CallRecordProgressBar(
            progress = progress,
            onProgressChange = { changedProgress ->
                onSeek((state.durationMillis * changedProgress).toLong())
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
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val inactiveColor = CallTheme.colors.gray100
    val activeColor = CallTheme.colors.mainVariant1

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .pointerInput(onProgressChange) {
                detectTapGestures { offset ->
                    onProgressChange((offset.x / size.width).coerceIn(0f, 1f))
                }
            },
    ) {
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
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
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
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
    ) {
        Icon(
            painter = painterResource(
                if (isPlaying) {
                    R.drawable.ic_home_call_pause
                } else {
                    R.drawable.ic_home_call_play
                },
            ),
            contentDescription = if (isPlaying) "일시정지" else "재생",
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

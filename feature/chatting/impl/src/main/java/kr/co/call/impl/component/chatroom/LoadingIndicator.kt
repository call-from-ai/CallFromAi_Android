package kr.co.call.impl.component.chatroom

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 세 개의 점이 위아래로 튀는 애니메이션을 표시하는 컴포저블입니다.
 * 채팅 인터페이스에서 "로딩 중" 상태를 나타내는 데 사용됩니다.
 *
 * @param modifier [Row] 레이아웃에 적용할 [Modifier]입니다.
 * @param dotColors 세 개의 점에 사용할 색상 목록입니다. 기본값은 회색 계열의 그라데이션입니다.
 * @param dotSize 각 점의 지름입니다.
 * @param spaceBetween 점 사이의 가로 간격입니다.
 * @param bounceHeight 애니메이션 동안 각 점이 위로 이동하는 최대 높이입니다.
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    dotColors: List<Color> = listOf(
        CallTheme.colors.gray200,
        CallTheme.colors.gray400,
        CallTheme.colors.gray600,
    ),
    dotSize: Dp = 8.dp,
    spaceBetween: Dp = 6.dp,
    bounceHeight: Dp = 3.dp,
) {
    Row(
        modifier = modifier.padding(top = bounceHeight), // 튀어오를 공간 확보
        horizontalArrangement = Arrangement.spacedBy(spaceBetween),
    ) {
        repeat(3) { index ->
            AnimatedDot(
                color = dotColors[index],
                size = dotSize,
                bounceHeight = bounceHeight,
                delayMillis = index * 150,
            )
        }
    }
}

/**
 * 위아래로 튀는 애니메이션을 수행하는 하나의 점을 나타냅니다.
 *
 * @param color 점의 색상입니다.
 * @param size 점의 지름입니다.
 * @param bounceHeight 점이 튀어 오를 때의 최대 세로 이동 거리입니다.
 * @param delayMillis 애니메이션 시작 지연 시간(밀리초)입니다.
 * 여러 점이 순차적으로 움직이는 효과를 만들기 위해 사용됩니다.
 */
@Composable
private fun AnimatedDot(
    color: Color,
    size: Dp,
    bounceHeight: Dp,
    delayMillis: Int,
) {
    val transition = rememberInfiniteTransition(label = "typing")

    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                0f at 0 using FastOutSlowInEasing
                1f at 300 using FastOutSlowInEasing   // 300ms에 최고점
                0f at 600 using FastOutSlowInEasing   // 600ms에 원위치
                // 600~900ms는 대기(idle)
            },
            initialStartOffset = StartOffset(delayMillis),
        ),
        label = "dotOffset",
    )

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                translationY = -progress * bounceHeight.toPx()
            }
            .clip(CircleShape)
            .background(color),
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadingIndicatorPreview() {
    CallFromAiTheme {
        LoadingIndicator()
    }
}

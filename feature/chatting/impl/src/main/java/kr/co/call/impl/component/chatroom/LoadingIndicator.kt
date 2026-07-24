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
        // 점이 위로 이동할 때 잘리지 않도록 상단 여백 확보
        modifier = modifier.padding(top = bounceHeight),
        horizontalArrangement = Arrangement.spacedBy(spaceBetween),
    ) {
        repeat(3) { index ->
            // 각 점마다 시작 지연 시간을 다르게 적용하여
            // 순차적으로 튀는 타이핑 인디케이터 효과 생성
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
    // 각 점마다 독립적인 무한 반복 애니메이션을 생성
    // delayMillis를 통해 점마다 애니메이션 시작 시점을 다르게 설정
    val transition = rememberInfiniteTransition(label = "typing")

    // 0 -> 1 -> 0 형태의 진행 값을 생성
    // progress 값은 실제 이동 거리 계산에 사용
    // 최고점에서는 bounceHeight만큼 위로 이동
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                // 애니메이션 시작
                0f at 0 using FastOutSlowInEasing

                // 300ms 후 최고점 도달
                1f at 300 using FastOutSlowInEasing

                // 600ms 후 원위치 복귀
                0f at 600 using FastOutSlowInEasing

                // 600~900ms는 대기(idle)
            },
            initialStartOffset = StartOffset(delayMillis),
        ),
        label = "dotOffset",
    )

    Box(
        modifier = Modifier
            .size(size)
            // progress 값에 따라 Y축 이동 적용
            // progress가 1일 때 가장 위로 이동하고 0일 때 원위치
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

package kr.co.call.impl.component

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * 스와이프하여 배경 콘텐츠를 드러내는 기능을 제공하는 컴포저블 컴포넌트입니다.
 *
 * 전경에 있는 [content]를 가로 방향으로 스와이프하여 뒤에 숨겨진 [backgroundContent]를 보여줍니다.
 * 스와이프 동작은 [AnchoredDraggableState]를 통해 관리되며, 주로 '스와이프하여 삭제'나
 * 추가 액션 메뉴를 보여주는 디자인 패턴에 사용됩니다.
 */
@Composable
fun SwipeableBox(
    modifier: Modifier = Modifier,
    swipeState: AnchoredDraggableState<SwipeState>,
    swipeWidth: Dp = 204.dp,
    backgroundContent: @Composable BoxScope.() -> Unit,
    content: @Composable () -> Unit
) {
    val swipeWidthPx = with(LocalDensity.current) { swipeWidth.toPx() }
    val offsetX = swipeState.offset
    val clampedOffset = if (offsetX.isNaN()) 0 else offsetX.roundToInt()
    val backgroundAlpha = if (swipeWidthPx == 0f) 0f else (-clampedOffset / swipeWidthPx).coerceIn(0f, 1f)

    LaunchedEffect(Unit) {
        swipeState.updateAnchors(
            DraggableAnchors {
                SwipeState.Closed at 0f
                SwipeState.Opened at -swipeWidthPx
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .anchoredDraggable(
                state = swipeState,
                orientation = Orientation.Horizontal
            )
    ) {
        Box(modifier = Modifier.align(Alignment.CenterEnd).alpha(backgroundAlpha)) {
            backgroundContent()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(clampedOffset, 0) }
        ) {
            content()
        }
    }
}
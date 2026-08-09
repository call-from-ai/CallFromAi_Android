package kr.co.call.impl.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * 화면 전체를 덮어 터치를 흡수한다. 다른 인터랙티브 요소를 이 컴포저블보다
 * 나중에(위 레이어에) 배치하면 그 요소는 정상적으로 클릭된다.
 */
@Composable
fun ScreenBlocker(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
    )
}

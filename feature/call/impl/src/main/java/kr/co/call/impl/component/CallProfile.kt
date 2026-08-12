package kr.co.call.impl.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kr.co.call.designsystem.R
import kr.co.call.designsystem.component.ProfileImage
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 원 하나가 spring 애니메이션으로 커졌다 작아지기를 무한 반복하는 scale 값
 * - delayMillis: 다른 원과 시작 시점을 어긋나게 해 파문처럼 겹쳐 보이게 함
 */
@Composable
private fun rememberPulseScale(
    minScale: Float,
    maxScale: Float,
    delayMillis: Long,
): Float {
    val scale = remember { Animatable(minScale) }
    LaunchedEffect(minScale, maxScale, delayMillis) {
        delay(delayMillis)
        while (isActive) {
            scale.animateTo(
                targetValue = maxScale,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessVeryLow,
                ),
            )
            scale.animateTo(
                targetValue = minScale,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessVeryLow,
                ),
            )
        }
    }
    return scale.value
}

/**
 * 통화 상대 프로필
 * - 통화 상태에 따라 미세하게 다르게 표시되기 때문에 스타일을 분리
 */
enum class CallProfileStyle(
    val outerSize: Dp,
    val innerSize: Dp,
    val imageSize: Dp,
    val badgeSize: Dp,
    val badgeOffset: Dp,
) {
    INCOMING(
        outerSize = 301.dp,
        innerSize = 242.dp,
        imageSize = 200.dp,
        badgeSize = 75.dp,
        badgeOffset = 71.dp,
    ),
    SESSION(
        outerSize = 209.dp,
        innerSize = 168.dp,
        imageSize = 139.dp,
        badgeSize = 52.dp,
        badgeOffset = 50.dp,
    ),
    INCOMING_COMPACT(
        outerSize = 224.dp,
        innerSize = 180.dp,
        imageSize = 149.dp,
        badgeSize = 56.dp,
        badgeOffset = 53.dp,
    ),
    SESSION_COMPACT(
        outerSize = 176.dp,
        innerSize = 142.dp,
        imageSize = 117.dp,
        badgeSize = 44.dp,
        badgeOffset = 42.dp,
    );

    companion object {
        fun incoming(isCompact: Boolean): CallProfileStyle =
            if (isCompact) INCOMING_COMPACT else INCOMING

        fun session(isCompact: Boolean): CallProfileStyle =
            if (isCompact) SESSION_COMPACT else SESSION
    }
}

@Composable
fun CallProfile(
    profileImageUrl: String?,
    style: CallProfileStyle,
    modifier: Modifier = Modifier,
) {
    val outerScale = rememberPulseScale(minScale = 1f, maxScale = 1.06f, delayMillis = 0L)
    val innerScale = rememberPulseScale(minScale = 1f, maxScale = 1.1f, delayMillis = 150L)

    Box(
        modifier = modifier
            .size(style.outerSize)
            .graphicsLayer {
                scaleX = outerScale
                scaleY = outerScale
            }
            .background(
                color = CallTheme.colors.mainVariant2.copy(alpha = 0.72f),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(style.innerSize)
                .graphicsLayer {
                    scaleX = innerScale
                    scaleY = innerScale
                }
                .background(
                    color = CallTheme.colors.mainVariant3.copy(alpha = 0.26f),
                    shape = CircleShape,
                ),
        )
        ProfileImage(
            profileImageUrl = profileImageUrl,
            size = DpSize(style.imageSize, style.imageSize),
        )
        Image(
            painter = painterResource(R.drawable.img_call_logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(
                    x = style.badgeOffset,
                    y = style.badgeOffset,
                )
                .size(style.badgeSize),
        )
    }
}

@Preview(showBackground = true, widthDp = 333, heightDp = 542)
@Composable
private fun CallProfilePreview() {
    CallFromAiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CallProfile(
                profileImageUrl = null,
                style = CallProfileStyle.INCOMING,
            )
            CallProfile(
                profileImageUrl = null,
                style = CallProfileStyle.SESSION,
            )
        }
    }
}

package kr.co.call.impl.component

import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme

/**
 * 그라데이션 효과를 적용한 배경
 * - 그라데이션을 비율로 위치 및 크기 설정
 * - 가운데를 기점으로 꽉 차는 원 모양의 그라데이션을 가로,세로 비율에 맞게 생성하도록 설정
 * - 그라데이션 목록을 리스트로 받아서 타원으로 표시
 */

// 그라데이션 타원 비율 고정
private const val GRADIENT_RADIUS_RATIO = 0.5209f

// 그라데이션 end 색상은 고정이므로 투명도 포함 지정
private val GRADIENT_END_COLOR = Color(0xFFFFF6FA).copy(alpha = 0f)

// 그라데이션 타원
private data class GradientBlob(
    val startColor: Color,
    val widthRatio: Float,
    val heightRatio: Float,
    val leftRatio: Float,
    val topRatio: Float,
)

// 그라데이션 위치 및 크기 비율 설정
private val GRADIENT_BLOBS = listOf(
    GradientBlob(
        startColor = Color(0xFFFF8099),
        widthRatio = 2.1650f,
        heightRatio = 1.0632f,
        leftRatio = -0.0485f,
        topRatio = 0.3511f,
    ),
    GradientBlob(
        startColor = Color(0xFFFFA8CF),
        widthRatio = 1.9549f,
        heightRatio = 1.1363f,
        leftRatio = -0.3155f,
        topRatio = 0.1167f,
    ),
    GradientBlob(
        startColor = Color(0xFFFF8099),
        widthRatio = 2.1650f,
        heightRatio = 1.1363f,
        leftRatio = -0.6068f,
        topRatio = 0.6543f,
    ),
    GradientBlob(
        startColor = Color(0xFFFEE0B8).copy(alpha = 0.9f),
        widthRatio = 2.0364f,
        heightRatio = 1.1363f,
        leftRatio = 0.2427f,
        topRatio = 0f,
    ),
    GradientBlob(
        startColor = Color(0xFFFEE0B8).copy(alpha = 0.6f),
        widthRatio = 2.1650f,
        heightRatio = 1.1363f,
        leftRatio = 0.1942f,
        topRatio = 0.7797f,
    ),
)

/**
 * 특정 위치에 지정된 크기로 꽉 차는 타원형 그라데이션 브러시를 생성하는 함수
 * - 여러 개의 GradientBlob을 화면 곳곳에 배치하고 blur효과를 줌
 */
private fun radialBrush(startColor: Color, topLeft: Offset) = object : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val biggerDimension = maxOf(size.width, size.height)
        val absoluteCenter = Offset(
            topLeft.x + size.width / 2f,
            topLeft.y + size.height / 2f,
        )
        val shader = RadialGradientShader(
            colors = listOf(startColor, GRADIENT_END_COLOR),
            center = absoluteCenter,
            radius = biggerDimension * GRADIENT_RADIUS_RATIO,
        )
        // 원형 그라데이션을 가로,세로 비율에 맞춰서 조정
        val matrix = Matrix().apply {
            setScale(
                size.width / biggerDimension,
                size.height / biggerDimension,
                absoluteCenter.x,
                absoluteCenter.y,
            )
        }
        shader.setLocalMatrix(matrix)
        return shader
    }
}

// 그라데이션을 리스트로 받아서 타원으로 표시
private fun DrawScope.drawGradientBlobs(blobs: List<GradientBlob>) {
    blobs.forEach { blob ->
        val topLeft = Offset(blob.leftRatio * size.width, blob.topRatio * size.height)
        val blobSize = Size(blob.widthRatio * size.width, blob.heightRatio * size.height)
        drawOval(
            brush = radialBrush(blob.startColor, topLeft),
            topLeft = topLeft,
            size = blobSize,
        )
    }
}

@Composable
fun CallGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(50.dp),
        ) {
            drawGradientBlobs(GRADIENT_BLOBS)
        }
        content()
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun CallGradientBackgroundPreview() {
    CallFromAiTheme {
        CallGradientBackground {}
    }
}

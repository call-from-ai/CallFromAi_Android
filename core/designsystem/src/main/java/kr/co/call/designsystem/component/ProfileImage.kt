package kr.co.call.designsystem.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import kr.co.call.designsystem.theme.CallFromAiTheme

/**
 * 프로필 이미지를 불러오는 컴포넌트
 * - 공통 재사용 가능
 * - URL이 없거나 로딩에 실패하면 기본 이미지 표시
 * - 통화 배지 표시 여부 설정 가능
 */
@Composable
fun ProfileImage(
    profileImageUrl: String?,
    modifier: Modifier = Modifier,
    showCallBadge: Boolean = false,
) {
    val defaultProfile = painterResource(
        id = kr.co.call.designsystem.R.drawable.img_profile_url_default,
    )

    Box(
        modifier = modifier.size(
            width = 49.dp,
            height = 51.dp,
        ),
    ) {
        AsyncImage(
            model = profileImageUrl?.takeIf { it.isNotBlank() },
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(25.dp)),
            placeholder = defaultProfile,
            error = defaultProfile,
            fallback = defaultProfile,
            contentScale = ContentScale.Crop,
        )

        if (showCallBadge) {
            Image(
                painter = painterResource(
                    id = kr.co.call.designsystem.R.drawable.img_call_logo,
                ),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
                    .zIndex(1f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileImagePreview() {
    CallFromAiTheme {
        ProfileImage(
            profileImageUrl = null,
            showCallBadge = true,
        )
    }
}

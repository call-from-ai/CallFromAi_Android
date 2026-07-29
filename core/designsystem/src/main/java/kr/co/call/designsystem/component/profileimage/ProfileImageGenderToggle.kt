package kr.co.call.designsystem.component.profileimage

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kr.co.call.designsystem.modifier.noRippleClickable
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 성별 슬라이드 세그먼트 컨트롤
 *
 * ## 상태
 * - 클릭 즉시 내부 state를 갱신해 썸·텍스트 색이 바로 바뀝니다 (프리뷰·부모 지연에도 반응).
 * - 동시에 [onGenderChange]를 호출합니다. **이미지 목록 교체는 호출부 책임**입니다.
 * - 외부 [selectedGender]가 바뀌면 내부 state와 재동기화합니다.
 *
 */
@Composable
internal fun ProfileImageGenderToggle(
    selectedGender: ProfileImageGender,
    onGenderChange: (ProfileImageGender) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentGender by remember { mutableStateOf(selectedGender) }
    LaunchedEffect(selectedGender) {
        currentGender = selectedGender
    }

    val segmentWidth = 63.dp
    val trackHeight = 28.dp
    val thumbInset = 2.dp
    val trackShape = RoundedCornerShape(30.dp)
    val thumbShape = RoundedCornerShape(30.dp)

    val thumbOffset by animateDpAsState(
        targetValue = when (currentGender) {
            ProfileImageGender.MALE -> thumbInset
            ProfileImageGender.FEMALE -> thumbInset + segmentWidth
        },
        animationSpec = tween(durationMillis = 220),
        label = "genderThumbOffset",
    )

    fun select(gender: ProfileImageGender) {
        if (currentGender == gender) return
        currentGender = gender
        onGenderChange(gender)
    }

    Box(
        modifier = modifier
            .height(trackHeight)
            .width(segmentWidth * 2 + thumbInset * 2)
            .background(
                color = CallTheme.colors.gray100,
                shape = trackShape,
            )
            .clip(trackShape),
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .width(segmentWidth)
                .fillMaxHeight()
                .background(
                    color = CallTheme.colors.mainVariant2,
                    shape = thumbShape,
                )
                .border(
                    width = 1.dp,
                    color = CallTheme.colors.mainVariant1,
                    shape = thumbShape,
                ),
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = thumbInset)
                .zIndex(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GenderSegmentLabel(
                label = "남자",
                isSelected = currentGender == ProfileImageGender.MALE,
                onClick = { select(ProfileImageGender.MALE) },
                modifier = Modifier
                    .width(segmentWidth)
                    .fillMaxHeight(),
            )
            GenderSegmentLabel(
                label = "여자",
                isSelected = currentGender == ProfileImageGender.FEMALE,
                onClick = { select(ProfileImageGender.FEMALE) },
                modifier = Modifier
                    .width(segmentWidth)
                    .fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun GenderSegmentLabel(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            CallTheme.colors.mainVariant1
        } else {
            CallTheme.colors.gray800
        },
        animationSpec = tween(durationMillis = 220),
        label = "genderSegmentText",
    )

    Box(
        modifier = modifier.noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = if (isSelected) {
                CallTheme.typography.bodySmallBold
            } else {
                CallTheme.typography.bodySmall
            },
            color = textColor,
        )
    }
}


/**
 * Preview : 성별 토글
 * */
@Preview(showBackground = true)
@Composable
private fun ProfileImageGenderTogglePreview() {
    CallFromAiTheme {
        var gender by remember { mutableStateOf(ProfileImageGender.FEMALE) }
        Box(modifier = Modifier.padding(16.dp)) {
            ProfileImageGenderToggle(
                selectedGender = gender,
                onGenderChange = { gender = it },
            )
        }
    }
}

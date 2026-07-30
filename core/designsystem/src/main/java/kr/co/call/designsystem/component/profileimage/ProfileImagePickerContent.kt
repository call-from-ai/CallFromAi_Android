package kr.co.call.designsystem.component.profileimage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 프로필 사진 선택 본문 (성별 토글 + 캐러셀 / empty).
 */
@Composable
internal fun ProfileImagePickerContent(
    images: List<ProfileImageOption>,
    selectedGender: ProfileImageGender,
    selectedImageId: String?,
    onGenderChange: (ProfileImageGender) -> Unit,
    onImageSelected: (ProfileImageOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileImageGenderToggle(
            selectedGender = selectedGender,
            onGenderChange = onGenderChange,
        )

        Spacer(modifier = Modifier.height(ContentSectionGap))

        if (images.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CenterImageHeight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "선택 가능한 사진이 없습니다",
                    style = CallTheme.typography.bodySmall,
                    color = CallTheme.colors.gray400,
                )
            }
        } else {
            key(images.map { it.id }) {
                ProfileImageCarousel(
                    images = images,
                    selectedImageId = selectedImageId,
                    onImageSelected = onImageSelected,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private val ContentSectionGap = 17.dp

/**
 * Preview : 토글 + 캐러셀 본문
 */
@Preview(showBackground = true,)
@Composable
private fun ProfileImagePickerContentGenderSwitchPreview() {
    CallFromAiTheme {
        var selectedGender by remember { mutableStateOf(ProfileImageGender.FEMALE) }
        var selectedImageId by remember {
            mutableStateOf(ProfileImagePickerPreviewData.femaleImages.first().id)
        }
        val images = ProfileImagePickerPreviewData.imagesFor(selectedGender)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CallTheme.colors.white)
                .padding(vertical = 8.dp),
        ) {
            ProfileImagePickerContent(
                images = images,
                selectedGender = selectedGender,
                selectedImageId = selectedImageId,
                onGenderChange = { gender ->
                    selectedGender = gender
                    selectedImageId = ProfileImagePickerPreviewData.imagesFor(gender).first().id
                },
                onImageSelected = { selectedImageId = it.id },
            )
        }
    }
}

@Preview( name = "빈 콘텐츠", showBackground = true)
@Composable
private fun ProfileImagePickerContentEmptyPreview() {
    CallFromAiTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CallTheme.colors.white)
                .padding(vertical = 8.dp),
        ) {
            ProfileImagePickerContent(
                images = emptyList(),
                selectedGender = ProfileImageGender.MALE,
                selectedImageId = null,
                onGenderChange = {},
                onImageSelected = {},
            )
        }
    }
}
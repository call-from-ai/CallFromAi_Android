package kr.co.call.designsystem.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.designsystem.component.profileimage.ProfileImageOption
import kr.co.call.designsystem.component.profileimage.ProfileImagePickerContent
import kr.co.call.designsystem.component.profileimage.ProfileImagePickerPreviewData
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 프로필 사진 선택 공통 바텀시트
 *
 * 마이페이지·온보딩 등에서 동일 UI로 재사용합니다.
 * [ConfirmBottomSheet] 셸 + [ProfileImagePickerContent](토글·캐러셀) 조합입니다.
 *
 * 모델·캐러셀·토글 구현은
 * `kr.co.call.designsystem.component.profileimage` 패키지에 있습니다.
 *
 * ## 책임 경계
 * | 이 컴포넌트 | 호출부 (feature / ViewModel) |
 * |-------------|------------------------------|
 * | 바텀시트 UI, 토글·캐러셀 표시 | 시트 열림 여부 |
 * | 스와이프/탭에 따른 콜백 전달 | [images] 목록 (성별별 필터 포함) |
 * | | [selectedGender], [selectedImageId] 보관 |
 * | | 저장 API / 로컬 반영 |
 *
 * ## 호출 계약
 * 1. [images]는 **현재 성별에 해당하는 목록만** 넘깁니다. 성별 전환 시 목록을 갈아끼우세요.
 * 2. [onGenderChange]에서 성별 state를 바꾼 뒤, 새 목록의 첫 항목 등으로
 *    [selectedImageId]를 리셋하는 것을 권장합니다.
 * 3. [onImageSelected]는 페이지 확정마다 호출됩니다. 저장 전 선택 반영에 사용합니다.
 * 4. designsystem에 domain 모델·Repository를 넣지 마세요. [ProfileImageOption]으로 매핑합니다.
 *
 * - 사용 예시
 * ```kotlin
 * import kr.co.call.designsystem.component.bottomsheet.ProfileImagePickerBottomSheet
 * import kr.co.call.designsystem.component.profileimage.ProfileImageGender
 * import kr.co.call.designsystem.component.profileimage.ProfileImageOption
 *
 * if (state.showImagePicker) {
 *     ProfileImagePickerBottomSheet(
 *         images = state.pickerImages,
 *         selectedGender = state.pickerGender,
 *         selectedImageId = state.selectedImageId,
 *         onGenderChange = { viewModel.onPickerGenderChange(it) },
 *         onImageSelected = { viewModel.onPickerImageSelected(it) },
 *         onSaveClick = { viewModel.onSaveProfileImage() },
 *         onDismissRequest = { viewModel.dismissImagePicker() },
 *     )
 * }
 * ```
 *
 * @param images 현재 성별 후보 목록 (빈 목록이면 empty 문구 표시)
 * @param selectedGender 성별 토글 선택값
 * @param selectedImageId 현재 선택 이미지 ID. null이면 캐러셀 첫 페이지
 * @param onGenderChange 성별 변경. 목록 교체는 호출부에서 수행
 * @param onImageSelected 캐러셀 페이지 확정 시
 * @param onSaveClick 하단 저장 버튼
 * @param onDismissRequest 닫기 / 스크림 / 시스템 뒤로가기
 * @param title 시트 제목 (기본 2줄)
 * @param confirmText 확인 버튼 문구
 * @param isLoading 목록을 불러오는 중이면 캐러셀 자리에 스켈레톤 표시
 */
@Composable
fun ProfileImagePickerBottomSheet(
    images: List<ProfileImageOption>,
    selectedGender: ProfileImageGender,
    selectedImageId: String?,
    onGenderChange: (ProfileImageGender) -> Unit,
    onImageSelected: (ProfileImageOption) -> Unit,
    onSaveClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "원하는 사진을\n선택해주세요",
    confirmText: String = "저장하기",
    isLoading: Boolean = false,
) {
    ConfirmBottomSheet(
        title = title,
        onConfirmClick = onSaveClick,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmText = confirmText,
        sheetHeight = null,
    ) {
        ProfileImagePickerContent(
            images = images,
            selectedGender = selectedGender,
            selectedImageId = selectedImageId,
            onGenderChange = onGenderChange,
            onImageSelected = onImageSelected,
            isLoading = isLoading,
        )
    }
}


/**
 * Preview
 */
@Preview(
    showBackground = true
)
@Composable
private fun ProfileImagePickerModalBottomSheetPreview() {
    CallFromAiTheme {
        var selectedGender by remember { mutableStateOf(ProfileImageGender.FEMALE) }
        var selectedImageId by remember {
            mutableStateOf(ProfileImagePickerPreviewData.femaleImages.first().id)
        }
        val images = ProfileImagePickerPreviewData.imagesFor(selectedGender)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(800.dp)
                .background(CallTheme.colors.gray800.copy(alpha = 0.35f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(800.dp)
                    .background(CallTheme.colors.subGray2),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        color = CallTheme.colors.white,
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    ),
            ) {
                ConfirmBottomSheetContent(
                    title = "원하는 사진을\n선택해주세요",
                    confirmText = "저장하기",
                    onConfirmClick = {},
                    onDismissRequest = {},
                    sheetHeight = null,
                ) {
                    ProfileImagePickerContent(
                        images = images,
                        selectedGender = selectedGender,
                        selectedImageId = selectedImageId,
                        onGenderChange = { gender ->
                            selectedGender = gender
                            selectedImageId =
                                ProfileImagePickerPreviewData.imagesFor(gender).first().id
                        },
                        onImageSelected = { selectedImageId = it.id },
                    )
                }
            }
        }
    }
}

package kr.co.call.impl.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kr.co.call.designsystem.R
import kr.co.call.designsystem.component.LocalBottomBarPadding
import kr.co.call.designsystem.component.bottomsheet.ProfileImagePickerBottomSheet
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.BirthdayChoice
import kr.co.call.impl.component.CommonTopAppBar
import kr.co.call.impl.component.EditProfileNameFields
import kr.co.call.impl.component.MemberChoice
import kr.co.call.impl.viewmodel.EditProfileIntent
import kr.co.call.impl.viewmodel.EditProfileJob
import kr.co.call.impl.viewmodel.EditProfileSideEffect
import kr.co.call.impl.viewmodel.EditProfileState
import kr.co.call.impl.viewmodel.EditProfileViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

private val MBTI_OPTIONS = listOf(
    "INFP", "INFJ", "ENFP", "ENFJ",
    "INTP", "INTJ", "ENTP", "ENTJ",
    "ISFP", "ISFJ", "ESFP", "ESFJ",
    "ISTP", "ISTJ", "ESTP", "ESTJ",
)

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is EditProfileSideEffect.NavigateBack -> onBackClick()
            is EditProfileSideEffect.ShowMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    EditProfileScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

@Composable
private fun EditProfileScreenContent(
    state: EditProfileState,
    onIntent: (EditProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomBarPadding = LocalBottomBarPadding.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 앱바
            CommonTopAppBar(
                title = "프로필 수정",
                onBackClick = { onIntent(EditProfileIntent.ClickBack) },
            )

            when (val load = state.loadStatus) {
                is LoadStatus.Loading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = CallTheme.colors.mainVariant1)
                    }
                }
                is LoadStatus.Error -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = load.message,
                            style = CallTheme.typography.bodyMedium,
                            color = CallTheme.colors.gray600,
                        )
                    }
                }
                LoadStatus.Idle -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(39.dp))
                        // 프로필
                        EditProfileHeader(
                            imageUrl = state.imageUrl,
                            name = state.displayName,
                            onEditPhotoClick = { onIntent(EditProfileIntent.ClickEditPhoto) },
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                        // 이름 입력란
                        EditProfileNameFields(
                            lastName = state.lastName,
                            firstName = state.firstName,
                            onLastNameChange = { onIntent(EditProfileIntent.ChangeLastName(it)) },
                            onFirstNameChange = { onIntent(EditProfileIntent.ChangeFirstName(it)) },
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        // 생년 월일 입력란
                        BirthdayChoice(
                            modifier = Modifier.fillMaxWidth(),
                            selectedDate = state.birth,
                            onDateSelected = { onIntent(EditProfileIntent.SelectBirth(it)) },
                            required = false,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        // 직업 입력란
                        MemberChoice(
                            modifier = Modifier.fillMaxWidth(),
                            label = "직업",
                            selectedOption = state.job?.label.orEmpty(),
                            placeholder = "직업을 선택해주세요",
                            options = EditProfileJob.entries.map { it.label },
                            onOptionSelected = { selectedLabel ->
                                EditProfileJob.entries
                                    .firstOrNull { it.label == selectedLabel }
                                    ?.let { onIntent(EditProfileIntent.SelectJob(it)) }
                            },
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        // mbti 입력란
                        MemberChoice(
                            modifier = Modifier.fillMaxWidth(),
                            label = "MBTI",
                            selectedOption = state.mbti.orEmpty(),
                            placeholder = "MBTI를 선택해주세요",
                            options = MBTI_OPTIONS,
                            onOptionSelected = { onIntent(EditProfileIntent.SelectMbti(it)) },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // 하단 완료 버튼
            if (state.loadStatus is LoadStatus.Idle) {
                SecondaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = bottomBarPadding),
                    text = if (state.isSaving) "저장 중..." else "완료",
                    enabled = state.canComplete,
                    onClick = { onIntent(EditProfileIntent.ClickComplete) },
                )
            }
        }

        // 프로필 사진 선택 바텀 시트
        if (state.showImagePicker) {
            ProfileImagePickerBottomSheet(
                images = state.pickerImages,
                selectedGender = state.pickerGender,
                selectedImageId = state.selectedImageId,
                onGenderChange = { onIntent(EditProfileIntent.ChangePickerGender(it)) },
                onImageSelected = { onIntent(EditProfileIntent.SelectPickerImage(it)) },
                onSaveClick = { onIntent(EditProfileIntent.ConfirmPickerImage) },
                onDismissRequest = { onIntent(EditProfileIntent.DismissImagePicker) },
            )
        }
    }
}

// 프로필 섹션
@Composable
private fun EditProfileHeader(
    imageUrl: String,
    name: String,
    onEditPhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 프로필 사진
        Box(modifier = Modifier.size(120.dp)) {
            AsyncImage(
                model = imageUrl.takeIf { it.isNotBlank() },
                contentDescription = "프로필 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                placeholder = painterResource(id = R.drawable.img_mypage_profile_default),
                error = painterResource(id = R.drawable.img_mypage_profile_default),
            )
            // 프로필 수정 아이콘
            Icon(
                painter = painterResource(id = R.drawable.ic_mypage_edit),
                contentDescription = "프로필 사진 수정",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .clickable(onClick = onEditPhotoClick),
                tint = androidx.compose.ui.graphics.Color.Unspecified,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // 사용자 이름
        Text(
            text = name.ifBlank { " " },
            style = CallTheme.typography.titleSmallBold,
            color = CallTheme.colors.black,
        )
    }
}

/**
 * Preview
 * */
@Preview(
    showBackground = true
)
@Composable
private fun EditProfileScreenPreview() {
    CallFromAiTheme {
        EditProfileScreenPreviewMock()
    }
}

@Composable
private fun EditProfileScreenPreviewMock() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CallTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        CommonTopAppBar(
            title = "프로필 수정",
            onBackClick = {},
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(39.dp))
            EditProfileHeader(
                imageUrl = "",
                name = "김수현",
                onEditPhotoClick = {},
            )
            Spacer(modifier = Modifier.height(28.dp))
            EditProfileNameFields(
                lastName = "김",
                firstName = "수현",
                onLastNameChange = {},
                onFirstNameChange = {},
            )
            Spacer(modifier = Modifier.height(24.dp))
            PreviewStaticField(
                label = "생년월일",
                value = "2001 / 5 / 18",
            )
            Spacer(modifier = Modifier.height(24.dp))
            PreviewStaticField(
                label = "직업",
                value = "대학생",
            )
            Spacer(modifier = Modifier.height(24.dp))
            PreviewStaticField(
                label = "MBTI",
                value = "ENFJ",
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        SecondaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 18.dp, top = 8.dp),
            text = "완료",
            enabled = true,
            onClick = {},
        )
    }
}

@Composable
private fun PreviewStaticField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = CallTheme.typography.bodyMedium,
            color = CallTheme.colors.gray600,
        )
        Spacer(modifier = Modifier.height(7.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(51.dp)
                .background(
                    color = CallTheme.colors.gray100,
                    shape = RoundedCornerShape(10.dp),
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = value,
                style = CallTheme.typography.bodyMedium,
                color = CallTheme.colors.gray900,
            )
        }
    }
}

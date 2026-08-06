package kr.co.call.impl.screen

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.co.call.designsystem.component.bottomsheet.ProfileImagePickerBottomSheet
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.designsystem.component.profileimage.ProfileImageOption
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray400
import kr.co.call.designsystem.theme.Gray600
import kr.co.call.impl.component.BirthdayChoice
import kr.co.call.impl.component.MemberChoice
import kr.co.call.impl.component.MessageInputField
import kr.co.call.impl.component.NameBox
import kr.co.call.impl.component.ProfileChoice
import kr.co.call.impl.component.TopTitle
import kr.co.call.impl.viewmodel.OnboardingViewModel
import kr.co.call.impl.viewmodel.model.Mbti
import kr.co.call.impl.viewmodel.model.MemberJob
import kr.co.call.impl.viewmodel.state.Onboarding1State
import kr.co.call.impl.viewmodel.state.ProfileChoiceState

private enum class EditingNameField {
    LAST_NAME,
    FIRST_NAME,
}
@Composable
fun Onboarding1Screen (
    viewModel: OnboardingViewModel,
    onNext: () -> Unit,
    onBackClick:()->Unit,
    modifier: Modifier =Modifier,
    profileImageUrl: String?=null,
) {
    BackHandler {
        onBackClick()
    }
    val uiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val initialBirthday = uiState.userBirthday
    val malePresetImages = uiState.presetImageState.maleImages.map { image ->
        ProfileImageOption(
            id = image.id.toString(),
            imageUrl = image.imageUrl,
        )
    }
    val femalePresetImages = uiState.presetImageState.femaleImages.map { image ->
        ProfileImageOption(
            id = image.id.toString(),
            imageUrl = image.imageUrl,
        )
    }

    var lastName by rememberSaveable {
        mutableStateOf("")
    }
    var firstName by rememberSaveable {
        mutableStateOf("")
    }
    var birthday by remember(initialBirthday) {
        mutableStateOf(initialBirthday)
    }
    var selectedJob by rememberSaveable {
        mutableStateOf<MemberJob?>(null)
    }
    var mbti by rememberSaveable {
        mutableStateOf("")
    }
    var editingNameField by rememberSaveable {
        mutableStateOf<EditingNameField?>(null)
    }

    var nameInput by rememberSaveable {
        mutableStateOf("")
    }
    var showProfileImagePicker by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedGender by rememberSaveable {
        mutableStateOf(ProfileImageGender.MALE)
    }
    LaunchedEffect(Unit) {
        viewModel.loadPresetImages(ProfileImageGender.MALE)
    }
    val currentProfileImages = when (selectedGender) {
        ProfileImageGender.MALE -> malePresetImages
        ProfileImageGender.FEMALE -> femalePresetImages
    }

    var selectedMaleImageId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var selectedFemaleImageId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val currentSelectedImageId = when (selectedGender) {
        ProfileImageGender.MALE -> selectedMaleImageId
        ProfileImageGender.FEMALE -> selectedFemaleImageId
    }

    var savedProfileImageUrl by rememberSaveable {
        mutableStateOf(profileImageUrl)
    }


    val canMoveNext =
        lastName.isNotBlank() &&
            firstName.isNotBlank() &&
            selectedJob!=null
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(
                    start = 27.dp,
                    end = 27.dp,
                    top = 54.dp,
                    bottom = 18.dp,
                ),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                TopTitle(
                    title = "먼저 나를\n소개해볼까요?",
                    description = "전화왔어에서 사용할 나의 프로필을 완성해주세요.",
                    horizontalPadding = 0.dp,
                )
                Spacer(modifier = Modifier.height(23.dp))
                Text(
                    text="사진선택",
                    style= CallTheme.typography.bodyMedium,
                    color= Gray600,
                )
                Spacer(modifier = Modifier.height(18.dp))
                ProfileChoice(
                    state= ProfileChoiceState(
                    imageUrl = savedProfileImageUrl,
                        size=99.dp,
                    ),
                    onClick = {
                        when (selectedGender) {
                            ProfileImageGender.MALE -> {
                                if (selectedMaleImageId == null) {
                                    selectedMaleImageId = currentProfileImages.firstOrNull()?.id
                                }
                            }

                            ProfileImageGender.FEMALE -> {
                                if (selectedFemaleImageId == null) {
                                    selectedFemaleImageId = currentProfileImages.firstOrNull()?.id
                                }
                            }
                        }
                        showProfileImagePicker = true
                    },
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    NameBox(
                        label = "성",
                        value = lastName,
                        onClick = {
                            nameInput = lastName
                            editingNameField = EditingNameField.LAST_NAME
                        },
                    )

                    NameBox(
                        label = "이름",
                        value = firstName,
                        onClick = {
                            nameInput = firstName
                            editingNameField = EditingNameField.FIRST_NAME
                        },
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                BirthdayChoice(
                    selectedDate = birthday,
                    onDateSelected = { newDate ->
                        birthday = newDate
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(24.dp))
                MemberChoice(
                    modifier = Modifier.fillMaxWidth(),
                    label = "직업",
                    selectedOption = selectedJob?.label.orEmpty(),
                    placeholder = "직업을 선택해주세요",
                    options = listOf("대학생", "직장인", "기타",),
                    onOptionSelected = {selectedLabel ->
                        selectedJob=MemberJob.entries.firstOrNull(){job->
                            job.label == selectedLabel
                        }
                    },
                    required = true,
                )
                Spacer(modifier = Modifier.height(24.dp))
                MemberChoice(
                    modifier = Modifier.fillMaxWidth(),
                    label = "MBTI",
                    selectedOption = mbti,
                    placeholder = "MBTI를 선택해주세요",
                    options = Mbti.entries.map { it.name },
                    onOptionSelected = { mbti = it },
                )
            }
            Column(
                modifier=Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "상세 정보는 나중에 다시 수정할 수 있어요!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom=5.dp),
                    color = Gray400,
                    style = CallTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
                SecondaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "다음",
                    enabled = canMoveNext,
                    onClick = {
                            val state = Onboarding1State(
                                lastName=lastName,
                                firstName=firstName,
                                birthday=birthday,
                                job=checkNotNull(selectedJob).name,
                                mbti=mbti,
                                gender=selectedGender.name,
                                imageUrl=savedProfileImageUrl.orEmpty(),
                            )
                            viewModel.updateUserProfile(
                                lastName = state.lastName,
                                firstName = state.firstName,
                                birthday = state.birthday,
                                job = state.job,
                                mbti = state.mbti,
                                gender = state.gender,
                                imageUrl = state.imageUrl,
                            )
                            onNext()
                    },
                )
            }
        }

    if (editingNameField != null) {
        // 배경 어둡게 처리
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.15f))
                .clickable {
                    when (editingNameField) {
                        EditingNameField.LAST_NAME -> {
                            lastName = nameInput
                        }

                        EditingNameField.FIRST_NAME -> {
                            firstName = nameInput
                        }

                        null -> Unit
                    }
                    editingNameField = null
                    nameInput = ""
                },
        )

        // 키보드 바로 위 입력창
        MessageInputField(
            value = nameInput,
            onValueChange = { input ->
                nameInput = input.take(10)
            },
            onSendClick = {
                when (editingNameField) {
                    EditingNameField.LAST_NAME -> {
                        lastName = nameInput
                    }

                    EditingNameField.FIRST_NAME -> {
                        firstName = nameInput
                    }
                    null -> Unit
                }

                editingNameField = null
                nameInput = ""
            },
            placeholder = when (editingNameField) {
                EditingNameField.LAST_NAME -> "성을 입력해주세요."
                EditingNameField.FIRST_NAME -> "이름을 입력해주세요."
                null -> ""
            },
            autoFocus = true,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 13.dp,
                ),
        )
    }
        if (showProfileImagePicker) {
            ProfileImagePickerBottomSheet(
                images = currentProfileImages,
                selectedGender = selectedGender,
                selectedImageId = currentSelectedImageId,
                onGenderChange = { newGender ->
                    selectedGender = newGender
                    viewModel.loadPresetImages(newGender)
                },
                onImageSelected = { image ->
                    when (selectedGender) {
                        ProfileImageGender.MALE -> selectedMaleImageId = image.id
                        ProfileImageGender.FEMALE -> selectedFemaleImageId = image.id
                    }
                },
                onSaveClick = {
                    savedProfileImageUrl = currentProfileImages
                        .firstOrNull { image ->
                            image.id == currentSelectedImageId
                        }
                        ?.imageUrl

                    showProfileImagePicker = false
                },
                onDismissRequest = {
                    showProfileImagePicker = false
                },
                title = "원하는 사진을\n선택해주세요",
                confirmText = "저장하기",
            )
        }
    }
}

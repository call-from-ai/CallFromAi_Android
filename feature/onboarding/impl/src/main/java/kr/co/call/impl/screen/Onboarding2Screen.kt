package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.bottomsheet.ProfileImagePickerBottomSheet
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.component.profileimage.ProfileImageGender
import kr.co.call.designsystem.component.profileimage.ProfileImageOption
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray400
import kr.co.call.designsystem.theme.Gray600
import kr.co.call.designsystem.theme.White
import kr.co.call.impl.component.AgeInputField
import kr.co.call.impl.component.BackStepBar
import kr.co.call.impl.component.MemberChoice
import kr.co.call.impl.component.MessageInputField
import kr.co.call.impl.component.NameBox
import kr.co.call.impl.component.ProfileChoice
import kr.co.call.impl.component.TopTitle
import kr.co.call.impl.viewmodel.model.CharacterJob
import kr.co.call.impl.viewmodel.model.Mbti
import kr.co.call.impl.viewmodel.state.Onboarding2State
import kr.co.call.impl.viewmodel.state.ProfileChoiceState


private enum class Onboarding2EditingNameField {
    LAST_NAME,
    FIRST_NAME,
}
@Composable
fun Onboarding2Screen(
    onBackClick: () -> Unit,
    onNextClick: (Onboarding2State) -> Unit,
    malePresetImages: List<ProfileImageOption>,
    femalePresetImages: List<ProfileImageOption>,
    onGenderChanged: (ProfileImageGender) ->Unit,
    modifier: Modifier = Modifier,
    profileImageUrl: String? = null,
) {
    var age by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var firstName by rememberSaveable { mutableStateOf("") }
    var selectedJob by rememberSaveable { mutableStateOf< CharacterJob?>(null) }
    var mbti by rememberSaveable { mutableStateOf("") }

    var editingNameField by rememberSaveable {
        mutableStateOf<Onboarding2EditingNameField?>(null)
    }
    var nameInput by rememberSaveable { mutableStateOf("") }
    //바텀시트 표시 여부
    var showProfileImagePicker by rememberSaveable {
        mutableStateOf(false)
    }
    //성별 선택
    var selectedGender by rememberSaveable {
        mutableStateOf(ProfileImageGender.MALE)
    }
    LaunchedEffect(Unit) {
        onGenderChanged(ProfileImageGender.MALE)
    }
    val currentProfileImages = when (selectedGender) {
        ProfileImageGender.MALE -> malePresetImages
        ProfileImageGender.FEMALE -> femalePresetImages
    }
    //캐러셀에서 보고 있는 사진
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
    //저장 버튼을 눌러 확정한 사진
    var savedProfileImageUrl by rememberSaveable {
        mutableStateOf(profileImageUrl)
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val canMoveNext = age.isNotBlank() &&
            lastName.isNotBlank() &&
            firstName.isNotBlank() &&
            selectedJob != null

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            BackStepBar(onBackClick = onBackClick)

            TopTitle(
                title = "내 이상형 설정,\n4단계면 끝나요",
                description = "함께할 상대방의 기본 정보를 입력해주세요.",
                currentStep = 1,
                totalStep = 4,
                horizontalPadding = 27.dp,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 27.dp),
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "사진 선택",
                    style = CallTheme.typography.bodyMedium,
                    color = Gray600,
                )

                Spacer(modifier = Modifier.height(18.dp))

                ProfileChoice(
                    state= ProfileChoiceState(
                    imageUrl = savedProfileImageUrl,
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

                AgeInputField(
                    age = age,
                    onAgeChange = { age = it },
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    NameBox(
                        label = "성",
                        value = lastName,
                        onClick = {
                            nameInput = lastName
                            editingNameField = Onboarding2EditingNameField.LAST_NAME
                        },
                    )

                    NameBox(
                        label = "이름",
                        value = firstName,
                        onClick = {
                            nameInput = firstName
                            editingNameField = Onboarding2EditingNameField.FIRST_NAME
                        },
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                MemberChoice(
                    modifier = Modifier.fillMaxWidth(),
                    label = "직업",
                    selectedOption = when (selectedJob) {
                        CharacterJob.UMEMPLOYED -> "기타"
                        else -> selectedJob?.label.orEmpty()
                    },
                    placeholder = "직업을 선택해주세요",
                    options = listOf("대학생", "직장인", "기타"),
                    onOptionSelected = {selectedLabel->
                        selectedJob = when (selectedLabel) {
                            "기타" -> CharacterJob.UMEMPLOYED
                            else -> CharacterJob.entries.firstOrNull { job ->
                                job.label == selectedLabel
                            }
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

                Spacer(modifier = Modifier.height(24.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 27.dp, vertical = 18.dp),
            ) {
                Text(
                    text = "상세 정보는 나중에 다시 수정할 수 있어요!",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = CallTheme.typography.caption,
                    color = Gray400,
                )

                Spacer(modifier = Modifier.height(9.dp))

                SecondaryButton(
                    text = "다음",
                    enabled = canMoveNext,
                    onClick = {
                        onNextClick(
                            Onboarding2State(
                                age=age,
                                lastName=lastName,
                                firstName=firstName,
                                job=checkNotNull(selectedJob).name,
                                mbti=mbti,
                                gender=selectedGender.name,
                                imageUrl=savedProfileImageUrl.orEmpty(),
                            )
                        )
                    },
                )
            }
        }

        if (editingNameField != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.15f))
                    .clickable {
                        editingNameField = null
                        nameInput = ""
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    },
            )

            MessageInputField(
                value = nameInput,
                onValueChange = { nameInput = it.take(10) },
                onSendClick = {
                    when (editingNameField) {
                        Onboarding2EditingNameField.LAST_NAME -> lastName = nameInput
                        Onboarding2EditingNameField.FIRST_NAME -> firstName = nameInput
                        null -> Unit
                    }

                    editingNameField = null
                    nameInput = ""
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                placeholder = when (editingNameField) {
                    Onboarding2EditingNameField.LAST_NAME -> "성을 입력해주세요."
                    Onboarding2EditingNameField.FIRST_NAME -> "이름을 입력해주세요."
                    null -> ""
                },
                autoFocus = true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 13.dp),
            )
        }
        if (showProfileImagePicker) {
            ProfileImagePickerBottomSheet(
                images = currentProfileImages,
                selectedGender = selectedGender,
                selectedImageId = currentSelectedImageId,

                onGenderChange = { newGender ->
                    selectedGender = newGender
                    onGenderChanged(newGender)
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


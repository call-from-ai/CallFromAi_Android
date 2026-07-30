package kr.co.call.impl.screen

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
import kr.co.call.designsystem.component.button.SecondaryButton
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
    onProfileClick: () -> Unit,
    onNextClick: (Onboarding2State) -> Unit,
    modifier: Modifier = Modifier,
    profileImageUrl: String? = null,
) {
    var age by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var firstName by rememberSaveable { mutableStateOf("") }
    var job by rememberSaveable { mutableStateOf("") }
    var mbti by rememberSaveable { mutableStateOf("") }

    var editingNameField by rememberSaveable {
        mutableStateOf<Onboarding2EditingNameField?>(null)
    }
    var nameInput by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val canMoveNext = age.isNotBlank() &&
            lastName.isNotBlank() &&
            firstName.isNotBlank() &&
            job.isNotBlank()

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
                    imageUrl = profileImageUrl,
                    ),
                    onClick = onProfileClick,
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
                    selectedOption = job,
                    placeholder = "직업을 선택해주세요",
                    options = listOf("대학생", "직장인", "기타"),
                    onOptionSelected = { job = it },
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
                                job=job,
                                mbti=mbti,
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
    }
}

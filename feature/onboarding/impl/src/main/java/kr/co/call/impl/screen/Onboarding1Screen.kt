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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.button.SecondaryButton
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

private enum class EditingNameField {
    LAST_NAME,
    FIRST_NAME,
}
@Composable
fun Onboarding1Screen (
    onProfileClick:()->Unit,
    onNextClick: (
        lastName: String,
        firstName: String,
        birthday: LocalDate,
        job: String,
        mbti: String,
            )->Unit,
    modifier: Modifier =Modifier,
    profileImageURl: String?=null,
) {
    var lastName by rememberSaveable {
        mutableStateOf("")
    }
    var firstName by rememberSaveable {
        mutableStateOf("")
    }
    var birthday by remember {
        mutableStateOf<LocalDate?>(null)
    }
    var job by rememberSaveable {
        mutableStateOf("")
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
    val canMoveNext =
        lastName.isNotBlank() &&
            firstName.isNotBlank() &&
            birthday != null &&
            job.isNotBlank()
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
                    modifier = Modifier,
                    imageUrl = profileImageURl,
                    onClick = onProfileClick,
                    size = 99.dp,
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
                    selectedOption = job,
                    placeholder = "직업을 선택해주세요",
                    options = listOf("대학생", "직장인", "기타",),
                    onOptionSelected = { job = it },
                    required = true,
                )
                Spacer(modifier = Modifier.height(24.dp))
                MemberChoice(
                    modifier = Modifier.fillMaxWidth(),
                    label = "MBTI",
                    selectedOption = mbti,
                    placeholder = "MBTI를 선택해주세요",
                    options = listOf(
                        "INFP", "INFJ", "ENFP", "ENFJ",
                        "INTP", "INTJ", "ENTP", "ENTJ",
                        "ISFP", "ISFJ", "ESFP", "ESFJ",
                        "ISTP", "ISTJ", "ESTP", "ESTJ",
                    ),
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
                        birthday?.let { selectedBirthday ->
                            onNextClick(
                                lastName,
                                firstName,
                                selectedBirthday,
                                job,
                                mbti,
                            )
                        }
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
}
}

@Preview(
    name = "Onboarding 1 Screen",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
private fun Onboarding1ScreenPreview() {
    CallFromAiTheme {
        Onboarding1Screen(
            onProfileClick = {},
            onNextClick = { lastName, firstName, birthday, job, mbti ->
                // Preview에서는 동작하지 않음
            },
            profileImageURl = null,
        )
    }
}

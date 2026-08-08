package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray400
import kr.co.call.designsystem.theme.SubGray2
import kr.co.call.designsystem.theme.White
import kr.co.call.impl.component.BackStepBar
import kr.co.call.impl.component.RelationshipChoiceCard
import kr.co.call.impl.component.SpeechChoice
import kr.co.call.impl.component.TempControl
import kr.co.call.impl.component.TopTitle
import kr.co.call.impl.viewmodel.model.Relationship
import kr.co.call.impl.viewmodel.model.SpeechStyle
import kr.co.call.onboarding.impl.R


@Composable
fun Onboarding3Screen(
    onBackClick:()->Unit,
    onNextClick: (
        speechStyle: SpeechStyle,
        relationship: Relationship,
        temperature: Int,
    ) -> Unit,
    modifier: Modifier =Modifier,
) {
    var selectedSpeech by rememberSaveable {
        mutableStateOf<SpeechStyle?>(null)
    }
    var selectedRelationship by rememberSaveable {
        mutableStateOf<Relationship?>(null)
    }
    var temperature by rememberSaveable {
        mutableIntStateOf(50)
    }

    val canMoveNext =
        selectedSpeech != null && selectedRelationship != null
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White),
    ) {
        BackStepBar(
            onBackClick = onBackClick,
        )
        TopTitle(
            title = "어떤 사이로\n시작할까요?",
            description = "원하는 상대방과의 관계를 선택해주세요.",
            currentStep = 2,
            totalStep = 4,
            horizontalPadding = 27.dp,
        )
        Spacer(modifier=Modifier.height(5.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SubGray2),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 27.dp),
            ) {
                Spacer(modifier = Modifier.height(26.dp))

                androidx.compose.material3.Text(
                    text = "어떤 말투로 대화할까요?",
                    style = CallTheme.typography.bodySmall,
                    color = Gray400,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(17.dp),
                ) {
                    SpeechChoice(
                        text = "반말",
                        selected = selectedSpeech == SpeechStyle.CASUAL,
                        onClick = { selectedSpeech = SpeechStyle.CASUAL },
                    )
                    SpeechChoice(
                        text = "반존대",
                        selected = selectedSpeech == SpeechStyle.SEMI_FORMAL,
                        onClick = { selectedSpeech = SpeechStyle.SEMI_FORMAL },
                    )
                    SpeechChoice(
                        text = "존댓말",
                        selected = selectedSpeech == SpeechStyle.FORMAL,
                        onClick = { selectedSpeech = SpeechStyle.FORMAL },
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                androidx.compose.material3.Text(
                    text = "어떤 관계로 시작할까요?",
                    style = CallTheme.typography.bodySmall,
                    color = Gray400,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(11.dp),
                ) {
                    RelationshipChoiceCard(
                        modifier=Modifier.weight(1f),
                        imageRes = R.drawable.relationship_some,
                        title = "썸",
                        description = "설레는 시작",
                        selected = selectedRelationship == Relationship.SOME,
                        onClick = { selectedRelationship = Relationship.SOME },
                    )
                    RelationshipChoiceCard(
                        modifier=Modifier.weight(1f),
                        imageRes = R.drawable.relationship_first,
                        title = "연애 초기",
                        description = "달달한 사이",
                        selected = selectedRelationship == Relationship.EARLY_DATING,
                        onClick = { selectedRelationship = Relationship.EARLY_DATING },
                    )
                    RelationshipChoiceCard(
                        modifier=Modifier.weight(1f),
                        imageRes = R.drawable.relationship_old,
                        title = "오래된 연인",
                        description = "편안한 관계",
                        selected = selectedRelationship == Relationship.LONG_TERM,
                        onClick = { selectedRelationship = Relationship.LONG_TERM },
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                TempControl(
                    modifier = Modifier.fillMaxWidth(),
                    onTemperatureChange = { temperature = it },
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            SecondaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        SubGray2
                    )
                    .padding(
                        start = 27.dp,
                        end = 27.dp,
                        bottom = 10.dp,
                    ),
                text = "다음",
                enabled = canMoveNext,
                onClick = {
                    onNextClick(
                        selectedSpeech!!,
                        selectedRelationship!!,
                        temperature,
                    )
                },
            )
        }
    }
}

@Preview(
    name = "Onboarding 3 Screen",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun Onboarding3ScreenPreview() {
    CallFromAiTheme {
        Onboarding3Screen(
            onBackClick = {},
            onNextClick = { _, _, _ ->
                // Preview에서는 화면 이동을 하지 않습니다.
            },
        )
    }
}

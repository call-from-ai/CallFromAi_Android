package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.SubGray2
import kr.co.call.designsystem.theme.White
import kr.co.call.domain.model.onboarding.CharacterOnboardingInput
import kr.co.call.domain.model.onboarding.CreatedCharacter
import kr.co.call.domain.model.onboarding.MemberOnboardingInput
import kr.co.call.domain.model.onboarding.PresetImage
import kr.co.call.domain.repository.OnboardingRepository
import kr.co.call.impl.component.BackStepBar
import kr.co.call.impl.component.KeywordChoice
import kr.co.call.impl.component.TopTitle
import kr.co.call.impl.viewmodel.OnboardingViewModel
import kr.co.call.impl.viewmodel.model.Trait

private val traitRows=listOf(
    listOf(Trait.HUMOROUS, Trait.PLAYFUL),
    listOf(Trait.AFFECTIONATE, Trait.JEALOUS,Trait.TALKATIVE),
    listOf(Trait.DAD_JOKE_LOVER, Trait.HOMEBODY),
    listOf(Trait.TEASING, Trait.POSSESSIVE, Trait.TSUNDERE),
    listOf(Trait.EXPRESSIVE, Trait.PET_NAME_LOVER),
    listOf(Trait.EXCLUSIVE, Trait.QUIRKY, Trait.LAID_BACK),
    listOf(Trait.OPENLY_JEALOUS, Trait.SHY),
    listOf(Trait.SMOOTH_TALKER, Trait.FREQUENT_CHECKER),
    listOf(Trait.GOOD_LISTENER, Trait.COMPLIMENTER),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Onboarding4Screen(
    onBackClick: () -> Unit,
    onNextClick: (List<Trait>) -> Unit,
    initialSelectedKeywords: List<String> = emptyList(),
    modifier: Modifier = Modifier,
) {
    var selectedKeywords by rememberSaveable(initialSelectedKeywords) {
        mutableStateOf(initialSelectedKeywords)
    }

    val selectedTraits = selectedKeywords.mapNotNull(Trait::fromKeyword)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White),
    ) {
        BackStepBar(onBackClick = onBackClick)

        TopTitle(
            title = "어떤 매력에\n끌리나요?",
            description = "우선 순위에 따라 최대 5개를 선택해주세요.",
            currentStep = 3,
            totalStep = 4,
            horizontalPadding = 27.dp,
        )
        Spacer(modifier = Modifier.height(25.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SubGray2),
        ) {
            FlowRow(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 23.dp,
                        bottom = 23.dp,
                    ),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                traitRows.flatten().forEach { trait ->
                    val selectedOrder = selectedKeywords
                        .indexOf(trait.keyword)
                        .takeIf { it >= 0 }
                        ?.plus(1)

                    KeywordChoice(
                        emoji = trait.emoji,
                        text = trait.label,
                        selectedOrder = selectedOrder,
                        onClick = {
                            selectedKeywords = when {
                                trait.keyword in selectedKeywords ->
                                    selectedKeywords - trait.keyword

                                selectedKeywords.size < 5 ->
                                    selectedKeywords + trait.keyword

                                else -> selectedKeywords
                            }
                        },
                    )
                }
            }

            SecondaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 27.dp,
                        end = 27.dp,
                        bottom = 10.dp,
                    ),
                text = "다음",
                enabled = selectedTraits.isNotEmpty(),
                showRipple = false,
                showPressedEffect = false,
                onClick = {
                    if (selectedTraits.isNotEmpty()) {
                        onNextClick(selectedTraits)
                    }
                },
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 800,
)
@Composable
private fun Onboarding2ScreenPreview() {
    val previewViewModel = remember {
        OnboardingViewModel(
            onboardingRepository = object : OnboardingRepository {
                override suspend fun submitMemberOnboarding(
                    submission: MemberOnboardingInput,
                ): Result<Unit> {
                    return Result.success(Unit)
                }

                override suspend fun submitCharacterOnboarding(
                    submission: CharacterOnboardingInput,
                ): Result<CreatedCharacter> {
                    return Result.success(
                        CreatedCharacter(
                            id = 1L,
                            name = "미리보기",
                        ),
                    )
                }

                override suspend fun getPresetImages(
                    gender: String,
                ): Result<List<PresetImage>> {
                    return Result.success(emptyList())
                }
            },
        )
    }

    CallFromAiTheme {
        Onboarding2Screen(
            viewModel = previewViewModel,
            onBackClick = {},
            onNext = {},
        )
    }
}

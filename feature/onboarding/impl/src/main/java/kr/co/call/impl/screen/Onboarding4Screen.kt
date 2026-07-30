package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.SubGray2
import kr.co.call.designsystem.theme.White
import kr.co.call.impl.component.BackStepBar
import kr.co.call.impl.component.KeywordChoice
import kr.co.call.impl.component.TopTitle
import kr.co.call.impl.viewmodel.model.Trait

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
        Spacer(modifier=Modifier.height(5.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SubGray2)
                .navigationBarsPadding(),
        ) {
            FlowRow(
                modifier = Modifier.padding(
                    start = 27.dp,
                    end = 27.dp,
                    top = 20.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
                maxItemsInEachRow = 3,
            ) {
                Trait.entries.forEach { trait ->
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
            Spacer(modifier = Modifier.weight(1f))
            SecondaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 27.dp,
                        end = 27.dp,
                        bottom = 18.dp,
                    ),
                text = "다음",
                enabled=selectedTraits.isNotEmpty(),
                onClick = { if(selectedTraits.isNotEmpty()) {
                    onNextClick(selectedTraits)
                }
                          },
            )
        }
    }
}

@Preview(
    name = "Onboarding 4 - 선택됨",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun Onboarding4ScreenPreview() {
    CallFromAiTheme {
        Onboarding4Screen(
            onBackClick = {},
            onNextClick = {},
            initialSelectedKeywords = listOf(
                Trait.HUMOROUS.keyword,
                Trait.PLAYFUL.keyword,
                Trait.AFFECTIONATE.keyword,
            ),
        )
    }
}
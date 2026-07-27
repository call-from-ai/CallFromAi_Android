package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.component.button.SecondaryButton
import kr.co.call.designsystem.theme.SubGray2
import kr.co.call.designsystem.theme.White
import kr.co.call.impl.component.BackStepBar
import kr.co.call.impl.component.PreferTime
import kr.co.call.impl.component.TopTitle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import kr.co.call.designsystem.theme.CallFromAiTheme

@Composable
fun Onboarding5Screen(
    onBackClick: () -> Unit,
    onNextClick: (PreferTime) -> Unit,
    initialPreferTime: PreferTime? = null,
    modifier: Modifier = Modifier,
){
    var selectedPreferTime by rememberSaveable {
    mutableStateOf<PreferTime?>(null)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = 54.dp,
                bottom = 18.dp,
            )
            .background(White),
    ) {
        BackStepBar(onBackClick = onBackClick)

        TopTitle(
            title = "언제 먼저 통화를\n걸어드리면 좋을까요?",
            description = "편하게 통화하기 좋은 시간대를 선택해주세요.",
            currentStep = 4,
            totalStep = 4,
            horizontalPadding = 27.dp,
        )

        Spacer(modifier = Modifier.height(5.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SubGray2)
                .navigationBarsPadding(),
        ) {
            Column(
                modifier = Modifier.padding(
                    start = 27.dp,
                    end = 27.dp,
                    top = 26.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                PreferTime.entries
                    .forEach { preferTime ->
                    PreferTime(
                        preferTime = preferTime,
                        selected = selectedPreferTime == preferTime,
                        onClick = { selectedPreferTime = preferTime },
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            SecondaryButton(
                modifier = Modifier.padding(
                    start = 27.dp,
                    end = 27.dp,
                    bottom = 18.dp,
                ),
                text = "다음",
                onClick = { selectedPreferTime?.let(onNextClick) },
            )
        }
    }
}

@Preview(
    name = "Onboarding 5",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun Onboarding5ScreenPreview() {
    CallFromAiTheme {
        Onboarding5Screen(
            onBackClick = {},
            onNextClick = {},
            initialPreferTime = PreferTime.DAY,
        )
    }
}
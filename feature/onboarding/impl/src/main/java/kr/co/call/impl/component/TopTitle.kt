package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray100
import kr.co.call.designsystem.theme.Gray400
import kr.co.call.designsystem.theme.MainGradient
import kr.co.call.designsystem.theme.MainVariant2
import kr.co.call.designsystem.theme.MainVariant3

@Composable
fun TopTitle(
    modifier: Modifier= Modifier,
    title: String,
    description: String,
    currentStep: Int?=null,
    totalStep: Int?=null,
    horizontalPadding: Dp =14.dp,
    ) {
    val progress = if (
        currentStep != null && totalStep != null && totalStep > 0
    ) {
        (currentStep.toFloat() / totalStep.toFloat()).coerceIn(0f, 1f)
    } else {
        null
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (progress != null) {
            Box(
                modifier= Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Gray100)
            ){
                Box(
                    modifier= Modifier
                        .fillMaxWidth(progress)
                        .clip(
                            RoundedCornerShape(
                                topEnd = 100.dp,
                                bottomEnd = 100.dp),
                        )
                        .fillMaxHeight()
                        .background(brush= MainGradient),
                )
            }
            Spacer(
                modifier = Modifier.height(23.dp),
            )
        }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start=horizontalPadding,
                        end = horizontalPadding),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = title,
                    style = CallTheme.typography.titleMediumBold,
                    color = Black,
                    modifier = Modifier.weight(1f),
                )

                if (currentStep != null && totalStep != null) {
                    Spacer(modifier= Modifier.padding(4.dp))
                    Text(
                        text="$currentStep/$totalStep",
                        style= CallTheme.typography.bodyMediumMedium,
                        color= MainVariant3,
                    )
                }
            }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = description,
            modifier=Modifier.padding(start=horizontalPadding),
            style = CallTheme.typography.bodySmall,
            color = Gray400,
        )
        }
    }

@Preview(
    name = "1단계 - 진행바 없음",
    showBackground = true,
)
@Composable
private fun TopTitleStep1Preview() {
    CallFromAiTheme {
        TopTitle(
            title = "먼저 나를\n소개해볼까요?",
            description = "전화왔어에서 사용할 나의 프로필을 완성해주세요.",
        )
    }
}

@Preview(
    name = "2단계",
    showBackground = true,
)
@Composable
private fun TopTitleStep2Preview() {
    CallFromAiTheme {
        TopTitle(
            title = "어떤 사이로\n시작할까요?",
            description = "원하는 상대방과의 관계를 선택해주세요.",
            currentStep = 2,
            totalStep = 4,
        )
    }
}

@Preview(
    name = "3단계",
    showBackground = true,
)
@Composable
private fun TopTitleStep3Preview() {
    CallFromAiTheme {
        TopTitle(
            title = "어떤 매력에\n끌리나요?",
            description = "우선 순위에 따라 최대 5개를 선택해주세요.",
            currentStep = 3,
            totalStep = 4,
        )
    }
}

@Preview(
    name = "4단계",
    showBackground = true,
)
@Composable
private fun TopTitleStep4Preview() {
    CallFromAiTheme {
        TopTitle(
            title = "언제 먼저 통화를\n걸어드리면 좋을까요?",
            description = "편하게 통화하기 좋은 시간대를 선택해주세요.",
            currentStep = 4,
            totalStep = 4,
        )
    }
}
package kr.co.call.impl.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray400
import kr.co.call.designsystem.theme.MainVariant2

@Composable
fun TopTitle(
    modifier: Modifier= Modifier,
    title: String,
    description: String,
    currentStep: Int?=null,
    totalStep: Int?=null,
    ){
    Column(
        modifier = modifier.fillMaxWidth(),
    ){
        Row(
            modifier= Modifier
                .fillMaxWidth()
                .padding(end=14.dp),
            verticalAlignment=Alignment.CenterVertically,
        ){
            Text(
                text=title,
                style=CallTheme.typography.titleMediumBold,
                color=Black,
                modifier=Modifier.weight(1f, fill=true),
            )

            if (currentStep != null && totalStep != null){
                StepNumber(currentStep=currentStep, totalStep = totalStep)
            }
        }
        Spacer(modifier=Modifier.height(3.dp))
        Text(
            text=description,
            style= CallTheme.typography.bodySmall,
            color= Gray400,
        )
    }
}

@Composable
fun StepNumber(
    currentStep: Int,
    totalStep: Int,
){
    Text(
        text="$currentStep/$totalStep",
        style= CallTheme.typography.bodyMediumMedium,
        color=MainVariant2,
    )
}

@Preview(showBackground = true)
@Composable
fun TopTitlePreviewWithBadge() {
    CallFromAiTheme {
        TopTitle(
            title = "어떤 매력에\n끌리나요?",
            description = "우선 순위에 따라 최대 5개를 선택해주세요.",
            currentStep = 3,
            totalStep = 4,
        )
    }

}

@Preview(showBackground = true)
@Composable
fun TopTitlePreviewWithoutBadge() {
    CallFromAiTheme {
        TopTitle(
            title = "먼저 나를\n소개해볼까요?",
            description = "전화왔어요에서 사용할 나의 프로필을 완성해주세요.",
        )
    }
}
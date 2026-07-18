package kr.co.call.impl.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme.typography
import kr.co.call.designsystem.theme.Gray100
import kr.co.call.designsystem.theme.Gray600
import kr.co.call.designsystem.theme.Gray900
import kr.co.call.designsystem.theme.MainVariant1
import kr.co.call.designsystem.theme.White
import kr.co.call.impl.component.AgreementItem
import kr.co.call.impl.component.CheckBox
import kr.co.call.impl.component.NextButton
import kr.co.call.impl.viewmodel.AgreementUiState
import kr.co.call.login.impl.R

enum class AgreementType(
    val title: String,
    val isRequired: Boolean,
){
    SERVICE(
        title="서비스 이용 약관 동의",
        isRequired = true,
    ),
    PRIVACY(
        title="개인정보 수집/이용 동의",
        isRequired = true,
    ),
    MARKETING(
        title="마케팅 정보 수신 동의",
        isRequired = false,
    ),
    DATA_USAGE(
        title="대화 데이터의 서비스 개선 활용 동의",
        isRequired = false,
    ),
}

@Composable
fun AgreementScreen(
    modifier: Modifier,
    uiState: AgreementUiState,
    onNextClick:()->Unit,
    onAgreementViewClick:(AgreementType)->Unit,
    onAgreementToggle: (AgreementType)->Unit,
    onAllAgreementsCheckedChange: (Boolean) -> Unit
) {
    val agreementTypes= AgreementType.entries
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 23.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(156.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "로고",
                    modifier = Modifier.size(70.dp),
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "전화왔어",
                    color = MainVariant1,
                    style = typography.titleExtraLargeBold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "전화왔어를 이용하려면\n약관에 동의가 필요해요",
                color = Gray900,
                style = typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(80.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                color = White,
                border = BorderStroke(
                    width = 1.dp,
                    color = Gray100,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 30.dp,
                    ),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CheckBox(
                            checked = uiState.isAllChecked,
                            onCheckedChange = onAllAgreementsCheckedChange,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "모두 동의",
                                color = Black,
                                style = typography.bodyMediumMedium
                            )
                            Text(
                                text = "서비스 이용을 위한 아래 약관에 모두 동의합니다",
                                color = Gray600,
                                style = typography.bodySmall
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Gray100,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    agreementTypes.forEach { agreementType ->
                        AgreementItem(
                            title = agreementType.title,
                            isRequired = agreementType.isRequired,
                            isChecked = agreementType in uiState.checkedAgreements,
                            onCheckedChange = {
                                onAgreementToggle(agreementType)
                            },
                            onViewClick = {
                                onAgreementViewClick(agreementType)
                            },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .navigationBarsPadding()
                .padding(
                    start = 23.dp,
                    end = 23.dp,
                    bottom = 25.dp,
                ),
        ) {
            NextButton(
                modifier=Modifier
                    .fillMaxWidth(),
                text="동의하고 프로필 설정하기",
                onClick = onNextClick,
                enabled = uiState.isRequiredChecked,
            )
        }
    }
}

@Preview(
    name = "Agreement Screen Preview",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun AgreementScreenPreview() {
    CallFromAiTheme {
        AgreementScreen(
            modifier = Modifier,
            uiState = AgreementUiState(),
            onAgreementToggle = {},
            onAllAgreementsCheckedChange = {},
            onAgreementViewClick = {},
            onNextClick = {},
        )
    }
}
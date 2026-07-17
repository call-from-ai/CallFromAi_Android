package kr.co.call.impl.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.call.designsystem.theme.CallTheme.typography
import kr.co.call.designsystem.theme.Pretendard
import kr.co.call.impl.component.AgreementSection
import kr.co.call.impl.component.BackTopBar

@Composable
fun AgreementDetailScreen(
    modifier: Modifier,
    title: String,
    content: String,
    onBackClick:()->Unit,
){
    val scrollState=rememberScrollState()
    Column(
        modifier=modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ){
        BackTopBar(
            onBackClick=onBackClick,
        )
        Column(
            modifier= Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(
                    horizontal =34.dp,
                    vertical=22.dp,
                    ),
        ){
            Text(
                text=title,
                fontSize =15.sp,
                fontFamily = Pretendard,
                fontWeight=FontWeight.Medium,
            )
            Spacer(
                modifier=Modifier.height(22.dp),
            )
            AgreementSection(
                content=content,
            )
            Spacer(modifier=Modifier.height(40.dp))
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun AgreementDetailScreenPreview() {
    AgreementDetailScreen(
        modifier=Modifier,
        title = "전화왔어 서비스 개선 활용 약관",
        content = """
대화 데이터의 서비스 개선 활용 동의
전화왔어는 AI 답변의 품질 향상과 서비스 개선을 위해 대화 데이터를 활용할 수 있습니다.

1. 활용 목적
-AI 답변 품질 개선
-오류 및 부적절한 응답 분석
-서비스 기능 개선 및 신규 기능 개발

2. 활용 정보
채팅 내용, 음성 변환 텍스트, AI 응답 내용, 서비스 이용 기록 등을 활용할 수 있습니다. 필요한 경우 개인을 직접 식별할 수 있는 정보는 제거하거나 가명 처리합니다.

3. 보유 및 이용 기간
동의 철회 또는 회원 탈퇴 시까지 보유·이용합니다. 다만 관련 법령에 따라 보관이 필요한 경우에는 정해진 기간 동안 보관할 수 있습니다.

4. 동의 거부 및 철회
본 동의는 선택 사항이며, 동의하지 않아도 전화왔어의 기본 서비스를 이용할 수 있습니다. 회원은 서비스 내 설정 또는 고객센터를 통해 언제든지 동의를 철회할 수 있습니다.

시행일: 2026년 7월 5일
""".trimIndent(),
        onBackClick = {},
    )
}
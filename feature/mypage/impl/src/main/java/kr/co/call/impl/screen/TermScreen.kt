package kr.co.call.impl.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.component.CommonTopAppBar

@Composable
fun TermScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        // 상단 앱바
        CommonTopAppBar(onBackClick = onBackClick, title = "약관/개인정보 처리 방침")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 40.dp),
        ) {
            Spacer(modifier= Modifier.height(32.dp))

            // 약관 내용
            /**TODO: 추후 하드코딩된 글자들 모두 string.xml으로 분리하기*/
            Text(
                text = "전화왔어 서비스 이용약관",
                style = CallTheme.typography.bodyMediumMedium,
                color = CallTheme.colors.gray800,
            )

            Spacer(modifier= Modifier.height(22.dp))

            Text(
                text = "최종 업데이트: 2025년 6월 25일\n" +
                        "본 약관은 감정 기록 기반 정서 일기 서비스 \"화록\"(이하 \"서비스\")의 이용과 관련하여, 화록을 운영하는 개발팀(이하 “운영자”)과 이용자 간의 권리, 의무 및 책임사항을 규정합니다.\n" +
                        "제1조 (목적)\n" +
                        "본 약관은 서비스의 이용조건 및 절차, 권리, 의무, 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다.\n" +
                        "제2조 (정의)\n" +
                        "\"회원\"은 카카오 로그인을 통해 본 서비스에 등록한 이용자를 말합니다.\n" +
                        "\"감정일기\"는 사용자가 감정 버튼을 선택하고 텍스트를 입력하여 작성한 기록을 의미합니다.\n" +
                        "\"리워드\"는 사용자가 일정 조건을 달성할 경우 제공되는 이미지 형태의 보상 아이템입니다.\n" +
                        "\"불 키우기\"는 친구에게 감정적 응원을 보내는 기능입니다.\n" +
                        "제3조 (약관의 효력 및 변경)\n" +
                        "본 약관은 앱 내 고지하거나 별도 페이지에 게시함으로써 효력을 발생합니다.\n" +
                        "운영자는 필요 시 약관을 변경할 수 있으며, 사전 공지를 원칙으로 합니다.\n" +
                        "변경된 약관에 동의하지 않을 경우 이용자는 탈퇴할 수 있습니다.\n" +
                        "제4조 (서비스 제공 및 변경)\n" +
                        "본 서비스는 감정일기 작성, 감정 분석, 감정 통계 제공, 친구 기능 등으로 구성됩니다.\n" +
                        "운영자는 서비스 내용을 변경하거나 중단할 수 있으며, 주요 변경 시 사전 고지합니다.",
                style = CallTheme.typography.bodySmall,
                color = CallTheme.colors.gray400,
            )

            Spacer(modifier= Modifier.height(29.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermScreenPreview() {
    CallFromAiTheme {
        TermScreen(onBackClick = {})
    }
}
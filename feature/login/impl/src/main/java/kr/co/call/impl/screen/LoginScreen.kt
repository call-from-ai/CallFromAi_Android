package kr.co.call.impl.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.call.designsystem.component.button.PrimaryButton
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallTheme.typography
import kr.co.call.designsystem.theme.Gray900
import kr.co.call.designsystem.theme.MainVariant1
import kr.co.call.designsystem.theme.Pretendard
import kr.co.call.designsystem.theme.White
import kr.co.call.login.impl.R

@Composable
fun LoginScreen(
    onKakaoLoginClick:()->Unit={},
    modifier: Modifier=Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(223.dp))
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "로고",
            modifier = Modifier.size(81.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "전화왔어",
            color = MainVariant1,
            fontSize = 34.sp,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(37.dp))
        Text(
            text = buildAnnotatedString {
                append("AI가 먼저 전화하고 관계를 이어가는,\n")
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                    ),
                ) {
                    append("나만의 연애 시뮬레이션")
                }
                append("\uD83D\uDC9E")
            },
            fontSize = 24.sp,
            color = Gray900,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(51.dp))
        KakaoLoginButton(
            onClick = onKakaoLoginClick,
        )
    }
}

@Composable
private fun KakaoLoginButton(
    onClick:()->Unit,
    modifier: Modifier=Modifier,
){
    Box(
        modifier=Modifier
            .fillMaxWidth(),
    ){
        PrimaryButton(
            modifier= Modifier
                .fillMaxWidth(),
            text="카카오로 3초 만에 시작하기",
            onClick=onClick,
            containerColor =Color(0xFFFDE500),
            contentColor = Black,
            pressedContainerColor = Color(0xFFFDE500),
        )
        Image(
            painter=painterResource(R.drawable.kakao),
            contentDescription = "카카오 로고",
            modifier=Modifier
                .align(Alignment.CenterStart)
                .padding(start=20.dp)
                .size(
                width=21.dp,
                height=20.1.dp
            ),
        )
    }
}

@Preview(
    showBackground=true,
    widthDp=393,
    heightDp=852,
)
@Composable
private fun LoginScreenPreview(){
    LoginScreen(
        onKakaoLoginClick={},
    )
}
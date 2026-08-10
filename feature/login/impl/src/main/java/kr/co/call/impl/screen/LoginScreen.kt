package kr.co.call.impl.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.designsystem.component.button.PrimaryButton
import kr.co.call.designsystem.theme.CallTheme.typography
import kr.co.call.designsystem.theme.MainVariant1
import kr.co.call.designsystem.theme.SubYellow
import kr.co.call.designsystem.theme.White
import kr.co.call.designsystem.theme.Black
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.auth.KakaoLoginManager
import kr.co.call.impl.viewmodel.LoginSideEffect
import kr.co.call.impl.viewmodel.LoginViewModel
import kr.co.call.login.impl.R
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    navigateToAgreement: (needsOnboarding: Boolean) -> Unit,
    navigateToOnboarding: (
        needsOnboarding: Boolean,
            needsTermsAgreement: Boolean,
            ) -> Unit,
    navigateToHome: (
        needsOnboarding: Boolean,
        needsTermsAgreement: Boolean,
            ) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val loginStatus by viewModel.collectAsState()
    val isLoading = loginStatus == LoadStatus.Loading

    val kakaoLoginManager = remember {
        KakaoLoginManager()
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoginSideEffect.NavigateToAgreement -> {
                navigateToAgreement(sideEffect.needsOnboarding)
            }

            is LoginSideEffect.NavigateToOnboarding -> {
                navigateToOnboarding(
                    sideEffect.needsOnboarding,
                    sideEffect.needsTermsAgreement,
                )
            }

            is LoginSideEffect.NavigateToHome -> {
                navigateToHome(
                    sideEffect.needsOnboarding,
                    sideEffect.needsTermsAgreement,
                )
            }

            is LoginSideEffect.ShowError -> {
                Toast.makeText(
                    context,
                    "서버 로그인 실패: ${sideEffect.message}",
                    Toast.LENGTH_SHORT,
                ).show()

                Timber.e("서버 로그인 실패: ${sideEffect.message}")
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(White),
    ) {
        Column(
            modifier=Modifier
                .fillMaxSize()
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
                style = typography.titleExtraLargeBold,
            )
            Spacer(modifier = Modifier.height(37.dp))
            Text(
                text = buildAnnotatedString {
                    append("AI가 먼저 전화하고 관계를 이어가는,\n")

                    withStyle(
                        style = typography.titleSmallBold.toSpanStyle(),
                    ) {
                        append("나만의 연애 시뮬레이션 \uD83D\uDC9E")
                    }
                },
                style = typography.titleSmall.copy(
                    lineHeight = 33.sp,
                ),
                color = Black,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(51.dp))
            KakaoLoginButton(
                enabled= !isLoading,
                onClick = {
                    kakaoLoginManager.login(
                        context = context,
                        onSuccess = viewModel::loginWithKakao,
                        onFailure = { error ->
                            Toast.makeText(
                                context,
                                error.message ?: "카카오 로그인에 실패했습니다",
                                Toast.LENGTH_SHORT,
                            ).show()

                            Timber.e(error, "카카오 SDK 로그인 실패")
                        },
                        onCancel = {
                            Timber.d("카카오 로그인 취소")
                        },
                    )
                },
            )
        }
        //로그인중 표시
        if (isLoading){
            Box(
                modifier= Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha=0.2f))
                    .clickable(
                        interactionSource=remember{
                            MutableInteractionSource()
                        },
                        indication=null,
                        onClick={},
                    ),
                contentAlignment=Alignment.Center,
            ){
                CircularProgressIndicator(
                    color=MainVariant1,
                )
            }
        }
    }
}

@Composable
private fun KakaoLoginButton(
    onClick:()->Unit={},
    enabled:Boolean=true,
){
    Box(
        modifier=Modifier.size(
            width=329.dp,
            height=50.dp,
        )
    ){
        PrimaryButton(
            modifier= Modifier
                .fillMaxWidth(),
            text="카카오로 3초 만에 시작하기",
            onClick={
                if (enabled){
                    onClick()
                }
            },
            containerColor = SubYellow,
            contentColor = Black,
            pressedContainerColor =SubYellow,
        )
        Image(
            painter=painterResource(R.drawable.kakao),
            contentDescription = "카카오 로고",
            modifier=Modifier
                .align(Alignment.CenterStart)
                .padding(start=20.dp)
                .size(
                width=21.dp,
                height=20.5.dp
            ),
        )
    }
}

package kr.co.call.impl.entry

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.AgreementDetailNavKey
import kr.co.call.api.AgreementNavKey
import kr.co.call.api.LandingNavKey
import kr.co.call.api.LoginNavKey
import kr.co.call.domain.model.login.AgreementTerm
import kr.co.call.impl.auth.KakaoLoginManager
import kr.co.call.impl.screen.AgreementDetailScreen
import kr.co.call.impl.screen.AgreementScreen
import kr.co.call.impl.screen.LandingScreen
import kr.co.call.impl.screen.LoginScreen
import kr.co.call.impl.viewmodel.AgreementSideEffect
import kr.co.call.impl.viewmodel.AgreementViewModel
import kr.co.call.impl.viewmodel.LandingSideEffect
import kr.co.call.impl.viewmodel.LandingViewModel
import kr.co.call.impl.viewmodel.LoginSideEffect
import kr.co.call.impl.viewmodel.LoginViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

/**
 * 랜딩, 로그인, 약관 화면을 Navigation3의 Entry로 등록한다.
 * 각 화면과 ViewModel을 연결하고 SideEffect에 따라 화면 이동을 처리한다.
 */
fun EntryProviderScope<NavKey>.loginEntry(
    navigateToLogin: ()->Unit,
    navigateToHome:()->Unit,
    navigateToAgreement:()->Unit,
    navigateToAgreementDetail:(AgreementTerm)->Unit,
    navigateAfterAgreement:()->Unit,
    onBack: ()->Unit,
) {
    entry<LandingNavKey> {
        val landingViewModel=hiltViewModel<LandingViewModel>()
        /**
         * 저장된 Access Token 확인 결과를 수집한다.
         * 토큰이 없으면 로그인 화면, 있으면 홈 화면으로 이동한다.
         */
        landingViewModel.collectSideEffect {sideEffect ->
            when (sideEffect){
                LandingSideEffect.NavigateToLogin->navigateToLogin()
                LandingSideEffect.NavigateToHome->navigateToHome()
            }
        }
        LandingScreen(
            modifier=Modifier,
        )
    }

    entry<LoginNavKey> {
        val context= LocalContext.current

        // 화면 재구성 시에도 같은 카카오 로그인 관리자 객체를 유지
        val kakaoLoginManager=remember{
            KakaoLoginManager()
        }
        val loginViewModel = hiltViewModel<LoginViewModel>()

        /**
         * 서버 로그인 결과를 수집한다.
         * 로그인 성공 시 약관 화면으로 이동하고 실패 시 오류를 기록한다.
         */
        loginViewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                LoginSideEffect.NavigateToNext -> {
                    navigateToAgreement()
                }

                is LoginSideEffect.ShowError -> {
                    Timber.e(sideEffect.message)
                }
            }
        }

        LoginScreen(
            onKakaoLoginClick={
                kakaoLoginManager.login(
                    context=context,
                    onSuccess=loginViewModel::loginWithKakao,
                    onFailure={error ->
                        Timber.e(error,"카카오 로그인 실패")
                    },
                    onCancel={Timber.d("카카오 로그인 취소")}
                )
            }
        )
    }

    entry<AgreementNavKey> {
        val agreementViewModel=hiltViewModel<AgreementViewModel>()
        val uiState=agreementViewModel.collectAsState().value

        agreementViewModel.collectSideEffect { sideEffect ->
            when (sideEffect){
                AgreementSideEffect.NavigateToNext->{
                    navigateAfterAgreement()
                }
                is AgreementSideEffect.ShowError ->{
                    Timber.e(sideEffect.message)
                }
            }
        }

        AgreementScreen(
            modifier=Modifier,
            uiState=uiState,
            onNextClick ={agreementViewModel.submitAgreements()},
            onAgreementViewClick = { agreementTerm ->
                navigateToAgreementDetail(agreementTerm)
            },
            onAgreementToggle = {termId ->
                agreementViewModel.toggleAgreement(
                    termId=termId,
                    )
            },
            onAllAgreementsCheckedChange = {isChecked ->
                agreementViewModel.toggleAllAgreements(isChecked=isChecked)
            },
        )
    }

    entry<AgreementDetailNavKey> { key ->
        AgreementDetailScreen(
            modifier = Modifier,
            title = key.title,
            content = key.content,
            onBackClick = onBack,
        )
    }
}

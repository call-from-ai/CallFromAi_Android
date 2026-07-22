package kr.co.call.impl.entry

import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.AgreementDetailNavKey
import kr.co.call.api.AgreementNavKey
import kr.co.call.api.LandingNavKey
import kr.co.call.api.LoginNavKey
import kr.co.call.domain.model.login.AgreementTerm
import kr.co.call.impl.screen.AgreementDetailScreen
import kr.co.call.impl.screen.AgreementScreen
import kr.co.call.impl.screen.LandingScreen
import kr.co.call.impl.screen.LoginScreen
import kr.co.call.impl.viewmodel.AgreementViewModel
import kr.co.call.impl.viewmodel.LoginSideEffect
import kr.co.call.impl.viewmodel.LoginViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

fun EntryProviderScope<NavKey>.loginEntry(
    navigateToLogin: ()->Unit,
    navigateToAgreement:()->Unit,
    navigateToAgreementDetail:(AgreementTerm)->Unit,
    navigateAfterAgreement:()->Unit,
    onBack: ()->Unit,
) {
    entry<LandingNavKey> {
        LandingScreen(
            modifier=Modifier,
            onTimeout=navigateToLogin,
        )
    }

    entry<LoginNavKey> {
        val loginViewModel = hiltViewModel<LoginViewModel>()
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
            onKakaoLoginSuccess = { kakaoAccessToken ->
                loginViewModel.loginWithKakao(
                    kakaoAccessToken = kakaoAccessToken,
                )
            },
        )
    }

    entry<AgreementNavKey> {
        val agreementViewModel=hiltViewModel<AgreementViewModel>()
        val uiState=agreementViewModel.collectAsState().value
        AgreementScreen(
            modifier=Modifier,
            uiState=uiState,
            onNextClick = navigateAfterAgreement,
            onAgreementViewClick = { agreementTerm ->
                navigateToAgreementDetail(agreementTerm)
            },
            onAgreementToggle = {termId ->
                agreementViewModel.toggleAgreement(
                    termId=termId,
                    )
            },
            onAllAgreementsCheckedChange = {isChecked ->
                agreementViewModel.toggleAllAgreements(
                    isChecked=isChecked,
                )
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

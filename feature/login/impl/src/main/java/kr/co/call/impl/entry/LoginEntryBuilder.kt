package kr.co.call.impl.entry

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.AgreementDetailNavKey
import kr.co.call.api.AgreementNavKey
import kr.co.call.api.LoginNavKey
import kr.co.call.designsystem.component.popup.TwoButtonPopup
import kr.co.call.domain.model.login.AgreementTerm
import kr.co.call.impl.auth.KakaoLoginManager
import kr.co.call.impl.screen.AgreementDetailScreen
import kr.co.call.impl.screen.AgreementScreen
import kr.co.call.impl.screen.LoginScreen
import kr.co.call.impl.viewmodel.AgreementSideEffect
import kr.co.call.impl.viewmodel.AgreementViewModel
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
    navigateToHome:()->Unit,
    navigateToAgreement:()->Unit,
    navigateToAgreementDetail:(AgreementTerm)->Unit,
    navigateAfterAgreement:()->Unit,
    onBack: ()->Unit,
    navigateToOnboarding: ()->Unit,
) {
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
                LoginSideEffect.NavigateToAgreement -> {
                    navigateToAgreement()
                }
                LoginSideEffect.NavigateToOnboarding ->{
                    navigateToOnboarding()
                }
                LoginSideEffect.NavigateToHome -> {
                    navigateToHome()
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

        LoginScreen(
            onKakaoLoginClick={
                kakaoLoginManager.login(
                    context=context,
                    onSuccess=loginViewModel::loginWithKakao,
                    onFailure={error ->
                        Toast.makeText(
                            context,
                            error.message ?: "카카오로그인에 실패했습니다",
                            Toast.LENGTH_SHORT,
                        ).show()
                        Timber.e(error,"카카오 SDK 로그인 실패")
                    },
                    onCancel={Timber.d("카카오 로그인 취소")}
                )
            }
        )
    }

    entry<AgreementNavKey> {
        val context=LocalContext.current

        val agreementViewModel=hiltViewModel<AgreementViewModel>()
        val uiState=agreementViewModel.collectAsState().value

        var isPopupDismissed by rememberSaveable {
            mutableStateOf(false)
        }
        val showNotificationPermissionPopup =
            !isPopupDismissed &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED

        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) {
            isPopupDismissed = true
        }

        agreementViewModel.collectSideEffect { sideEffect ->
            when (sideEffect){
                AgreementSideEffect.NavigateToNext->{
                    navigateAfterAgreement()
                }
                is AgreementSideEffect.ShowError ->{
                    Toast.makeText(
                        context,
                        sideEffect.message,
                        Toast.LENGTH_SHORT,
                    ).show()

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

        if (showNotificationPermissionPopup) {
            TwoButtonPopup(
                label = "",
                title = "‘전화왔어’에서 알림을\n보내고자 합니다.",
                description = AnnotatedString(
                    "경고, 사운드 및 아이콘 배지가 알림에\n" +
                        "포함될 수 있습니다.\n" +
                        "설정에서 이를 구성할 수 있습니다.",
                ),
                positiveText = "허용",
                negativeText = "허용 안 함",
                onPositiveClick = {
                    notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS,
                    )
                },
                onNegativeClick = {
                    isPopupDismissed = true
                },
                onDismissRequest = {
                    isPopupDismissed = true
                },
            )
        }
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

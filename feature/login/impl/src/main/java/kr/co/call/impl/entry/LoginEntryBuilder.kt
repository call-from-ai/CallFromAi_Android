package kr.co.call.impl.entry

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.AgreementDetailNavKey
import kr.co.call.api.AgreementNavKey
import kr.co.call.api.LoginNavKey
import kr.co.call.domain.model.login.AgreementTerm
import kr.co.call.impl.screen.AgreementDetailScreen
import kr.co.call.impl.screen.AgreementScreen
import kr.co.call.impl.screen.LoginScreen

/**
 * 랜딩, 로그인, 약관 화면을 Navigation3의 Entry로 등록한다.
 * 각 화면과 ViewModel을 연결하고 SideEffect에 따라 화면 이동을 처리한다.
 */
fun EntryProviderScope<NavKey>.loginEntry(
    navigateToHome:(needsOnboarding: Boolean)->Unit,
    navigateToAgreement:(needsOnboarding: Boolean)->Unit,
    navigateToAgreementDetail:(AgreementTerm)->Unit,
    navigateAfterAgreement:(needsOnboarding: Boolean)->Unit,
    onBack: ()->Unit,
    navigateToOnboarding: (needsOnboarding: Boolean)->Unit,
) {
    entry<LoginNavKey> {
        LoginScreen(
            navigateToAgreement=navigateToAgreement,
            navigateToOnboarding=navigateToOnboarding,
            navigateToHome=navigateToHome,
        )
    }

    entry<AgreementNavKey> { key ->
        AgreementScreen(
            onNavigateNext = {
                navigateAfterAgreement(key.needsOnboarding)
            },
            onAgreementViewClick = { agreementTerm ->
                navigateToAgreementDetail(agreementTerm)
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

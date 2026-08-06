package kr.co.call.impl.entry

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.Onboarding1NavKey
import kr.co.call.api.Onboarding2NavKey
import kr.co.call.api.Onboarding3NavKey
import kr.co.call.api.Onboarding4NavKey
import kr.co.call.api.Onboarding5NavKey
import kr.co.call.api.Onboarding6NavKey
import kr.co.call.api.OnboardingFlowMode
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.screen.Onboarding1Screen
import kr.co.call.impl.screen.Onboarding2Screen
import kr.co.call.impl.screen.Onboarding3Screen
import kr.co.call.impl.screen.Onboarding4Screen
import kr.co.call.impl.screen.Onboarding5Screen
import kr.co.call.impl.screen.Onboarding6Screen
import kr.co.call.impl.viewmodel.OnboardingSideEffect
import kr.co.call.impl.viewmodel.OnboardingViewModel
import org.orbitmvi.orbit.compose.collectSideEffect

fun EntryProviderScope<NavKey>.onboardingEntry(
    onOnboarding1Next: () -> Unit,
    onBackFromOnboarding2: () -> Unit,
    onOnboarding2Next: () -> Unit,
    onBackFromOnboarding3: () -> Unit,
    onOnboarding3Next: () -> Unit,
    onBackFromOnboarding4: () -> Unit,
    onOnboarding4Next: () -> Unit,
    onBackFromOnboarding5: () -> Unit,
    onOnboarding5Next: () -> Unit,
    onAdditionalCharacterCreated: () -> Unit,
    onOnboarding6CallNow: () -> Unit,
    onOnboarding6CallLater: () -> Unit,
) {
    entry<Onboarding1NavKey> {
        val onboardingViewModel = sharedOnboardingViewModel()

        LaunchedEffect(Unit) {
            onboardingViewModel.prepareFlow(OnboardingFlowMode.FIRST_ONBOARDING)
        }

        Onboarding1Screen(
            viewModel = onboardingViewModel,
            onNext = onOnboarding1Next,
        )
    }

    entry<Onboarding2NavKey> { key ->
        val onboardingViewModel = sharedOnboardingViewModel()
        val uiState by onboardingViewModel.container.stateFlow
            .collectAsStateWithLifecycle()

        // resetToken: 마이페이지 -> 추가 진입 시에만 draft 초기화
        // 2<->3 뒤로가기 복귀는 동일 key라 prepareFlow를 다시 타지 않음
        LaunchedEffect(key.mode, key.resetToken) {
            if (key.mode == OnboardingFlowMode.ADD_CHARACTER) {
                onboardingViewModel.prepareFlow(OnboardingFlowMode.ADD_CHARACTER)
            } else if (uiState.flowMode != OnboardingFlowMode.FIRST_ONBOARDING) {
                onboardingViewModel.prepareFlow(OnboardingFlowMode.FIRST_ONBOARDING)
            }
        }

        Onboarding2Screen(
            viewModel = onboardingViewModel,
            onBackClick = onBackFromOnboarding2,
            onNext = onOnboarding2Next,
        )
    }

    entry<Onboarding3NavKey> {
        val onboardingViewModel = sharedOnboardingViewModel()
        Onboarding3Screen(
            onBackClick = onBackFromOnboarding3,
            onNextClick = { speechStyle, relationship, temperature ->
                onboardingViewModel.updateConversationStyle(
                    speechStyle = speechStyle,
                    relationship = relationship,
                    temperature = temperature,
                )
                onOnboarding3Next()
            },
        )
    }

    entry<Onboarding4NavKey> {
        val onboardingViewModel = sharedOnboardingViewModel()
        Onboarding4Screen(
            onBackClick = onBackFromOnboarding4,
            onNextClick = { traits ->
                onboardingViewModel.updateTraits(traits)
                onOnboarding4Next()
            },
        )
    }

    entry<Onboarding5NavKey> {
        val context = LocalContext.current
        val onboardingViewModel = sharedOnboardingViewModel()
        val uiState by onboardingViewModel.container.stateFlow
            .collectAsStateWithLifecycle()
        onboardingViewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                OnboardingSideEffect.OnboardingSubmitted -> onOnboarding5Next()
                OnboardingSideEffect.AdditionalCharacterCreated -> onAdditionalCharacterCreated()
                is OnboardingSideEffect.ShowMessage -> {
                    Toast.makeText(
                        context,
                        sideEffect.message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
        Onboarding5Screen(
            onBackClick = onBackFromOnboarding5,
            onNextClick = { preferTime ->
                onboardingViewModel.submitOnboarding(preferTime)
            },
            isLoading = uiState.submitStatus == LoadStatus.Loading,
        )
    }

    entry<Onboarding6NavKey> {
        val onboardingViewModel = sharedOnboardingViewModel()
        val uiState by onboardingViewModel.container.stateFlow
            .collectAsStateWithLifecycle()
        Onboarding6Screen(
            characterName = uiState.createdAiName,
            isLoading = false,
            onCallNowClick = onOnboarding6CallNow,
            onCallLaterClick = onOnboarding6CallLater,
        )
    }
}

@Composable
private fun sharedOnboardingViewModel(): OnboardingViewModel {
    val activity = checkNotNull(LocalActivity.current as? ComponentActivity)

    return hiltViewModel(
        viewModelStoreOwner = activity,
    )
}

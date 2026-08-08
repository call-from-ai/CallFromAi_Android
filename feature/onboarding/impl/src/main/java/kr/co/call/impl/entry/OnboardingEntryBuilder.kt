package kr.co.call.impl.entry

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.Onboarding1NavKey
import kr.co.call.api.Onboarding2NavKey
import kr.co.call.api.OnboardingFlowMode
import kr.co.call.api.Onboarding3NavKey
import kr.co.call.api.Onboarding4NavKey
import kr.co.call.api.Onboarding5NavKey
import kr.co.call.api.Onboarding6NavKey
import kr.co.call.impl.screen.Onboarding1Screen
import kr.co.call.impl.screen.Onboarding2Screen
import kr.co.call.impl.screen.Onboarding3Screen
import kr.co.call.impl.screen.Onboarding4Screen
import kr.co.call.impl.screen.Onboarding5Screen
import kr.co.call.impl.screen.Onboarding6Screen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import kr.co.call.domain.util.LoadStatus
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
    onOnboarding6CallNow: (characterId: Long, characterName: String) -> Unit,
    onOnboarding6CallLater: () -> Unit,
) {
    entry<Onboarding1NavKey> {
        val onboardingViewModel = sharedOnboardingViewModel()
        Onboarding1Screen(
            viewModel = onboardingViewModel,
            onNext = onOnboarding1Next,
        )
    }

    entry<Onboarding2NavKey> { key ->
        val onboardingViewModel = sharedOnboardingViewModel()
        val uiState by onboardingViewModel.container.stateFlow
            .collectAsStateWithLifecycle()

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
            isCallDialogVisible = uiState.isCallDialogVisible,
            onShowCallDialog = onboardingViewModel::showCallDialog,
            onDismissCallDialog = onboardingViewModel::hideCallDialog,
            onCallNowClick = {
                val characterId = uiState.createdAiId
                if (characterId != null) {
                    onOnboarding6CallNow(characterId, uiState.createdAiName)
                } else {
                    // 캐릭터 생성 결과가 없으면 통화로 보낼 수 없어 홈으로 대체
                    onOnboarding6CallLater()
                }
            },
            onCallLaterClick = onOnboarding6CallLater,
        )
    }
}

@Composable
private fun sharedOnboardingViewModel(): OnboardingViewModel {
    val activity = checkNotNull(LocalActivity.current as? ComponentActivity)

    return hiltViewModel(
        viewModelStoreOwner =activity,
    )
}

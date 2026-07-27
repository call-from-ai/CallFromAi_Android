package kr.co.call.impl.entry

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.Onboarding1NavKey
import kr.co.call.api.Onboarding2NavKey
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
import kr.co.call.impl.viewmodel.OnboardingViewModel
import androidx.compose.runtime.getValue

fun EntryProviderScope<NavKey>.onboardingEntry(
    onboardingViewModel: OnboardingViewModel,
    onProfileClick: () -> Unit,
    onOnboarding1Next: () -> Unit,
    onBackFromOnboarding2: () -> Unit,
    onOnboarding2Next: () -> Unit,
    onBackFromOnboarding3: () -> Unit,
    onOnboarding3Next: () -> Unit,
    onBackFromOnboarding4: () -> Unit,
    onOnboarding4Next: () -> Unit,
    onBackFromOnboarding5: () -> Unit,
    onOnboarding5Next: () -> Unit,
    onOnboarding6CallNow: () -> Unit,
    onOnboarding6CallLater: () -> Unit,
) {
    entry<Onboarding1NavKey> {
        Onboarding1Screen(
            onProfileClick = onProfileClick,
            onNextClick = { lastName, firstName, birthday, job, mbti ->
                onboardingViewModel.updateUserProfile(
                    lastName = lastName,
                    firstName = firstName,
                    birthday = birthday,
                    job = job,
                    mbti = mbti,
                )
                onOnboarding1Next()
            },
        )
    }

    entry<Onboarding2NavKey> {
        Onboarding2Screen(
            onBackClick = onBackFromOnboarding2,
            onProfileClick = onProfileClick,
            onNextClick ={ age, lastName, firstName, job, mbti ->
                onboardingViewModel.updateAiProfile(
                    age = age,
                    lastName = lastName,
                    firstName = firstName,
                    job = job,
                    mbti = mbti,
                )

                onOnboarding2Next()
            },
        )
    }

    entry<Onboarding3NavKey> {
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
        Onboarding4Screen(
            onBackClick = onBackFromOnboarding4,
            onNextClick = { traits ->
                onboardingViewModel.updateTraits(traits)
                onOnboarding4Next()
            },
        )
    }

    entry<Onboarding5NavKey> {
        Onboarding5Screen(
            onBackClick = onBackFromOnboarding5,
            onNextClick = { preferTime ->
                onboardingViewModel.updatePreferTime(preferTime)
                onOnboarding5Next()
            },
        )
    }

    entry<Onboarding6NavKey> {
        val uiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
        Onboarding6Screen(
            firstName = uiState.aiFirstName,
            onCallNowClick = onOnboarding6CallNow,
            onCallLaterClick = onOnboarding6CallLater,
        )
    }
}

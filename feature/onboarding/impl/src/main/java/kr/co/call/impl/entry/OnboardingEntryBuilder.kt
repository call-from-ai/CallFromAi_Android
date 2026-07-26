package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.Onboarding1NavKey
import kr.co.call.api.Onboarding2NavKey
import kr.co.call.api.Onboarding3NavKey
import kr.co.call.api.Onboarding4NavKey
import kr.co.call.api.Onboarding5NavKey
import kr.co.call.impl.screen.Onboarding1Screen
import kr.co.call.impl.screen.Onboarding2Screen
import kr.co.call.impl.screen.Onboarding3Screen
import kr.co.call.impl.screen.Onboarding4Screen
import kr.co.call.impl.screen.Onboarding5Screen
import kr.co.call.impl.screen.Relationship
import kr.co.call.impl.screen.SpeechStyle
import java.time.LocalDate
import kr.co.call.impl.component.PreferTime
import kr.co.call.impl.component.Trait

fun EntryProviderScope<NavKey>.onboardingEntry(
    onProfileClick: () -> Unit,
    onOnboarding1Next: (
        lastName: String,
        firstName: String,
        birthday: LocalDate,
        job: String,
        mbti: String,
    ) -> Unit,
    onBackFromOnboarding2: () -> Unit,
    onOnboarding2Next: (
        age: String,
        lastName: String,
        firstName: String,
        job: String,
        mbti: String,
    ) -> Unit,
    onBackFromOnboarding3: () -> Unit,
    onOnboarding3Next: (
        speechStyle: SpeechStyle,
        relationship: Relationship,
        temperature: Int,
    ) -> Unit,
    onBackFromOnboarding4: () -> Unit,
    onOnboarding4Next: (List<Trait>) -> Unit,
    onBackFromOnboarding5: () -> Unit,
    onOnboarding5Next: (PreferTime) -> Unit,
) {
    entry<Onboarding1NavKey> {
        Onboarding1Screen(
            onProfileClick = onProfileClick,
            onNextClick = onOnboarding1Next,
        )
    }

    entry<Onboarding2NavKey> {
        Onboarding2Screen(
            onBackClick = onBackFromOnboarding2,
            onProfileClick = onProfileClick,
            onNextClick = onOnboarding2Next,
        )
    }

    entry<Onboarding3NavKey> {
        Onboarding3Screen(
            onBackClick = onBackFromOnboarding3,
            onNextClick = onOnboarding3Next,
        )
    }

    entry<Onboarding4NavKey> {
        Onboarding4Screen(
            onBackClick = onBackFromOnboarding4,
            onNextClick = onOnboarding4Next,
        )
    }

    entry<Onboarding5NavKey> {
        Onboarding5Screen(
            onBackClick = onBackFromOnboarding5,
            onNextClick = onOnboarding5Next,
        )
    }
}

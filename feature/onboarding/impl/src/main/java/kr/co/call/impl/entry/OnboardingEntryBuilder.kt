package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.OnboardingNavKey
import kr.co.call.impl.screen.OnboardingScreen

fun EntryProviderScope<NavKey>.onboardingEntry() {
    entry<OnboardingNavKey> {
        OnboardingScreen()
    }
}

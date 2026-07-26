package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.Onboarding1NavKey
import kr.co.call.api.Onboarding2NavKey
import kr.co.call.impl.screen.Onboarding1Screen
import kr.co.call.impl.screen.Onboarding2Screen
import java.time.LocalDate

fun EntryProviderScope<NavKey>.onboardingEntry(
    onProfileClick:()->Unit,
    onNextClick:(
        lastName: String,
        firstName: String,
        birthday: LocalDate,
        job: String,
        mbti: String,
            )->Unit,
) {
    entry<Onboarding1NavKey> {
        Onboarding1Screen(
            onProfileClick=onProfileClick,
            onNextClick=onNextClick
        )
    }
    entry<Onboarding2NavKey>{
        Onboarding2Screen()
    }
}

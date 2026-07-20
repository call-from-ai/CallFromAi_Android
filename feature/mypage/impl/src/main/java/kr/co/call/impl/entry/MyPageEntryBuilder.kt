package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.FaqNavKey
import kr.co.call.api.MyPageNavKey
import kr.co.call.api.TermNavKey
import kr.co.call.impl.screen.FaqScreen
import kr.co.call.impl.screen.MyPageScreen
import kr.co.call.impl.screen.TermScreen

fun EntryProviderScope<NavKey>.myPageEntry(
    navigateToFaq: () -> Unit,
    navigateToTerms: () -> Unit,
    onBack: () -> Unit = {},
) {
    entry<MyPageNavKey> {
        MyPageScreen(
            navigateToFaq = navigateToFaq,
            navigateToTerms = navigateToTerms,
        )
    }

    entry<FaqNavKey> {
        FaqScreen(onBackClick = onBack)
    }

    entry<TermNavKey> {
        TermScreen(onBackClick = onBack)
    }
}
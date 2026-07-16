package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.MyPageNavKey
import kr.co.call.api.ProfileNavKey
import kr.co.call.impl.screen.MyPageScreen
import kr.co.call.impl.screen.ProfileScreen

fun EntryProviderScope<NavKey>.myPageEntry(
    navigateToProfile: () -> Unit,
    navigateBack: () -> Unit,
) {
    entry<MyPageNavKey> {
        MyPageScreen(onNavigateToProfile = navigateToProfile)
    }
    entry<ProfileNavKey> {
        ProfileScreen(onBackClick = navigateBack)
    }
}
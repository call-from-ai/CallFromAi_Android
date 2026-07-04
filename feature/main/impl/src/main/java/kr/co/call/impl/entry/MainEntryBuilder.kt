package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.MainNavKey
import kr.co.call.impl.screen.MainScreen

fun EntryProviderScope<NavKey>.mainEntry() {
    entry<MainNavKey> {
        MainScreen()
    }
}

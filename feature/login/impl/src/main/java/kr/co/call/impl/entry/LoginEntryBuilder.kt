package kr.co.call.impl.entry

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.LoginNavKey
import kr.co.call.impl.screen.LoginScreen

fun EntryProviderScope<NavKey>.loginEntry() {
    entry<LoginNavKey> {
        LoginScreen()
    }
}

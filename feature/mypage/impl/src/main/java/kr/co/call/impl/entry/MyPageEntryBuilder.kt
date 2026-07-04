package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.MyPageNavKey
import kr.co.call.impl.screen.MyPageScreen

fun EntryProviderScope<NavKey>.myPageEntry() {
    entry<MyPageNavKey> {
        MyPageScreen()
    }
}

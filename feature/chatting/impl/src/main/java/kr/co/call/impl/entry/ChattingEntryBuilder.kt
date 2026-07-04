package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.ChattingNavKey
import kr.co.call.impl.screen.ChattingScreen

fun EntryProviderScope<NavKey>.chattingEntry() {
    entry<ChattingNavKey> {
        ChattingScreen()
    }
}

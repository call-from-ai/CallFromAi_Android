package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.ChattingNavKey
import kr.co.call.impl.screen.ChatListScreen

fun EntryProviderScope<NavKey>.chattingEntry() {
    entry<ChattingNavKey> {
        ChatListScreen()
    }
}

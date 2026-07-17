package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.ChatRoomNavKey
import kr.co.call.api.ChattingNavKey
import kr.co.call.impl.screen.ChatListScreen
import kr.co.call.impl.screen.ChatRoomScreen

fun EntryProviderScope<NavKey>.chattingEntry(
    navigateToChatRoom: (Long) -> Unit,
    onBack: () -> Unit = {}
) {
    entry<ChattingNavKey> {
        ChatListScreen(
            onChatRoomClick = navigateToChatRoom
        )
    }

    entry<ChatRoomNavKey> { key ->
        ChatRoomScreen(
            roomId = key.roomId,
            onBack = onBack
        )
    }
}

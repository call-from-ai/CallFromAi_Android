package kr.co.call.impl.entry

import androidx.compose.runtime.key
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.ChatRoomNavKey
import kr.co.call.api.ChattingNavKey
import kr.co.call.api.ManagerChatRoomNayKey
import kr.co.call.impl.screen.ChatListScreen
import kr.co.call.impl.screen.ChatRoomScreen
import kr.co.call.impl.screen.ManagerChatRoomScreen
import kr.co.call.impl.viewmodel.ChatRoomViewModel

fun EntryProviderScope<NavKey>.chattingEntry(
    navigateToChatRoom: (Long) -> Unit,
    navigateToManagerChatRoom: () -> Unit,
    navigateToCall: (characterId: Long, characterName: String, characterImageUrl: String?) -> Unit,
    onBack: () -> Unit = {}
) {
    entry<ChattingNavKey> {
        ChatListScreen(
            onChatRoomClick = navigateToChatRoom,
            onManagerChatRoomClick = navigateToManagerChatRoom
        )
    }

    entry<ChatRoomNavKey> { key ->
        val viewModel = hiltViewModel<ChatRoomViewModel, ChatRoomViewModel.Factory>(
            creationCallback = { factory -> factory.create(key) }
        )
        ChatRoomScreen(
            viewModel = viewModel,
            onBack = onBack,
            onNavigateToCall = navigateToCall,
        )
    }

    entry<ManagerChatRoomNayKey> {
        ManagerChatRoomScreen(
            onBack = onBack
        )
    }

}

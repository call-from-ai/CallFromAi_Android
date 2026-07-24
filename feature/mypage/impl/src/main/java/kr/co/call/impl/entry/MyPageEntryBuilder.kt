package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.CharacterManagementNavKey
import kr.co.call.api.MyPageNavKey
import kr.co.call.impl.screen.CharacterManagementScreen
import kr.co.call.impl.screen.MyPageScreen

fun EntryProviderScope<NavKey>.myPageEntry(
    navigateToCharacterManagement: () -> Unit,
    onBack: () -> Unit = {},
) {
    entry<MyPageNavKey> {
        MyPageScreen(
            onNavigateToCharacterManagement = navigateToCharacterManagement,
        )
    }

    entry<CharacterManagementNavKey> {
        CharacterManagementScreen(
            onBackClick = onBack,
            navigateToAddCharacter = { /* TODO: 캐릭터 추가 화면 연결 */ },
        )
    }
}

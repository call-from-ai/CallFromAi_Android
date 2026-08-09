package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.CallRecordNavKey
import kr.co.call.api.HomeNavKey
import kr.co.call.impl.screen.CallRecordScreen
import kr.co.call.impl.screen.HomeScreen

fun EntryProviderScope<NavKey>.homeEntry(
    navigateToCall: (characterId: Long, characterName: String, characterImageUrl: String?) -> Unit,
    navigateToCallRecord: (Long) -> Unit,
    navigateToCharacterOnboarding: () -> Unit,
    onCallRecordBack: () -> Unit,
) {
    entry<HomeNavKey> {
        HomeScreen(
            onNavigateToCall = navigateToCall,
            onNavigateToCallRecord = navigateToCallRecord,
            onNavigateToCharacterOnboarding = navigateToCharacterOnboarding,
        )
    }
    entry<CallRecordNavKey> { key ->
        CallRecordScreen(
            callId = key.callId,
            onBackClick = onCallRecordBack,
        )
    }
}

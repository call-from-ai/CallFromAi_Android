package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.CallRecordNavKey
import kr.co.call.api.HomeNavKey
import kr.co.call.impl.screen.CallRecordScreen
import kr.co.call.impl.screen.HomeScreen

fun EntryProviderScope<NavKey>.homeEntry(
    navigateToCallRecord: (Long) -> Unit,
    onCallRecordBack: () -> Unit,
) {
    entry<HomeNavKey> {
        HomeScreen(
            onNavigateToCallRecord = navigateToCallRecord,
        )
    }
    entry<CallRecordNavKey> { key ->
        CallRecordScreen(
            callId = key.callId,
            onBackClick = onCallRecordBack,
        )
    }
}

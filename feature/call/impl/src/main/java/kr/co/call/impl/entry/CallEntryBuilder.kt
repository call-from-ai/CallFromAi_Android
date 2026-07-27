package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.CallNavKey
import kr.co.call.impl.screen.CallScreen

fun EntryProviderScope<NavKey>.callEntry(
    onCallFinished: () -> Unit,
) {
    entry<CallNavKey> { key ->
        CallScreen(
            characterId = key.characterId,
            characterName = key.characterName,
            onCallFinished = onCallFinished,
        )
    }
}

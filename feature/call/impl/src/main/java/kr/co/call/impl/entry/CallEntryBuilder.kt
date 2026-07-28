package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.CallIncomingNavKey
import kr.co.call.api.CallSendingNavKey
import kr.co.call.impl.screen.CallScreen
import kr.co.call.impl.viewmodel.model.CallDirection

fun EntryProviderScope<NavKey>.callEntry(
    onCallFinished: () -> Unit,
) {
    entry<CallSendingNavKey> { key ->
        CallScreen(
            callId = 0L,
            characterId = key.characterId,
            direction = CallDirection.OUTGOING,
            onCallFinished = onCallFinished,
        )
    }

    entry<CallIncomingNavKey> { key ->
        CallScreen(
            callId = key.callId,
            characterId = key.characterId,
            direction = CallDirection.INCOMING,
            onCallFinished = onCallFinished,
        )
    }
}

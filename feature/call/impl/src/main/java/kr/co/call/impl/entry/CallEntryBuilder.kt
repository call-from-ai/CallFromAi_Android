package kr.co.call.impl.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.CallActiveNavKey
import kr.co.call.api.CallIncomingNavKey
import kr.co.call.api.CallSendingNavKey
import kr.co.call.impl.screen.CallIncomingScreen
import kr.co.call.impl.screen.CallScreen

fun EntryProviderScope<NavKey>.callEntry(
    onIncomingAccepted: (callId: Long, characterId: Long) -> Unit,
    onIncomingFinished: () -> Unit,
    onCallFinished: () -> Unit,
    onShowMessage: (String) -> Unit,
) {
    // 사용자가 아직 수락 또는 거절하지 않은 전체 착신 화면으로 이동
    entry<CallIncomingNavKey> { key ->
        CallIncomingScreen(
            callId = key.callId,
            characterId = key.characterId,
            characterName = key.characterName,
            characterImageUrl = key.characterImageUrl,
            onNavigateToCall = onIncomingAccepted,
            onFinished = onIncomingFinished,
            onShowMessage = onShowMessage,
        )
    }

    // 사용자가 직접 건 통화
    entry<CallSendingNavKey> { key ->
        CallScreen(
            characterId = key.characterId,
            characterName = key.characterName,
            characterImageUrl = key.characterImageUrl,
            isIncoming = false,
            onCallFinished = onCallFinished,
        )
    }

    // 모달 또는 전체 착신 화면에서 수락이 완료된 실제 통화
    entry<CallActiveNavKey> { key ->
        CallScreen(
            callId = key.callId,
            characterId = key.characterId,
            isIncoming = key.isIncoming,
            onCallFinished = onCallFinished,
        )
    }
}

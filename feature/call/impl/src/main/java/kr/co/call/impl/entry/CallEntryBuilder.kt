package kr.co.call.impl.entry

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kr.co.call.api.CallIncomingNavKey
import kr.co.call.api.CallSendingNavKey
import kr.co.call.impl.screen.CallScreen
import kr.co.call.impl.viewmodel.CallViewModel

fun EntryProviderScope<NavKey>.callEntry(
    onCallFinished: () -> Unit,
) {
    entry<CallSendingNavKey> { key ->
        val viewModel = hiltViewModel<CallViewModel, CallViewModel.Factory>(
            creationCallback = { factory -> factory.create(key) },
        )
        CallScreen(
            onCallFinished = onCallFinished,
            viewModel = viewModel,
        )
    }

    entry<CallIncomingNavKey> { key ->
        val viewModel = hiltViewModel<CallViewModel, CallViewModel.Factory>(
            creationCallback = { factory -> factory.create(key) },
        )
        CallScreen(
            onCallFinished = onCallFinished,
            viewModel = viewModel,
        )
    }
}

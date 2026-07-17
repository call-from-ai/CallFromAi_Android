package kr.co.call.impl.mapper

import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.CallSender
import kr.co.call.domain.model.home.CallStatus
import kr.co.call.impl.viewmodel.CallHistoryType
import kr.co.call.impl.viewmodel.state.HomeCallHistoryItemState

internal fun CallHistory.toUiState(): HomeCallHistoryItemState =
    HomeCallHistoryItemState(
        callId = callId,
        characterName = characterName,
        aiSummary = aiSummary,
        startedAtText = TimeUtil.toCallHistoryDateText(startedAt),
        type = toCallHistoryType(),
    )

private fun CallHistory.toCallHistoryType(): CallHistoryType =
    when {
        sender == CallSender.USER && status == CallStatus.COMPLETED ->
            CallHistoryType.SEND

        sender == CallSender.AI && status == CallStatus.COMPLETED ->
            CallHistoryType.RECEIVED

        sender == CallSender.USER && status == CallStatus.MISSED ->
            CallHistoryType.SEND_MISSED

        else ->
            CallHistoryType.RECEIVE_MISSED
    }

package kr.co.call.impl.mapper

import kr.co.call.core.common.util.TimeUtil
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.impl.viewmodel.model.CallHistoryIconType
import kr.co.call.impl.viewmodel.model.CallHistoryUiModel

fun CallHistory.toUiModel(): CallHistoryUiModel =
    CallHistoryUiModel(
        callId = callId,
        characterName = characterName,
        aiSummary = aiSummary,
        startedAtText = TimeUtil.toCallHistoryDateText(startedAt),
        iconType = toIconType(),
    )

private fun CallHistory.toIconType(): CallHistoryIconType =
    when {
        isMissed -> CallHistoryIconType.MISSED
        isOutgoing -> CallHistoryIconType.SENT
        else -> CallHistoryIconType.RECEIVED
    }

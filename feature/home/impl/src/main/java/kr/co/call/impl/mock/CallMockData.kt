package kr.co.call.impl.mock

import java.time.LocalDateTime
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.impl.mapper.toUiModel
import kr.co.call.impl.viewmodel.model.CallHistoryUiModel

internal object CallMockData {
    val histories: List<CallHistory> = listOf(
        CallHistory(
            callId = 1L,
            characterName = "민준",
            aiSummary = "오늘 하루와 퇴근 후 일상 이야기",
            startedAt = LocalDateTime.of(2026, 6, 28, 23, 2),
            isOutgoing = true,
            isMissed = false,
        ),
        CallHistory(
            callId = 2L,
            characterName = "민준",
            aiSummary = "출근 준비와 아침 일정 이야기",
            startedAt = LocalDateTime.of(2026, 6, 27, 7, 31),
            isOutgoing = false,
            isMissed = false,
        ),
        CallHistory(
            callId = 3L,
            characterName = "민준",
            aiSummary = "",
            startedAt = LocalDateTime.of(2026, 6, 26, 21, 29),
            isOutgoing = false,
            isMissed = true,
        ),
    )

    val uiModels: List<CallHistoryUiModel> =
        histories.map { callHistory -> callHistory.toUiModel() }
}

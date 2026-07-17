package kr.co.call.impl.mock

import java.time.LocalDateTime
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.CallSender
import kr.co.call.domain.model.home.CallStatus
import kr.co.call.domain.model.home.CallType
import kr.co.call.impl.mapper.toUiState
import kr.co.call.impl.viewmodel.state.CallHistoryState

internal object CallMockData {
    val histories: List<CallHistory> = listOf(
        CallHistory(
            callId = 1L,
            characterName = "민준",
            aiSummary = "오늘 하루와 퇴근 후 일상 이야기",
            startedAt = LocalDateTime.of(2026, 6, 28, 23, 2),
            sender = CallSender.USER,
            callType = CallType.DAILY,
            status = CallStatus.COMPLETED,
        ),
        CallHistory(
            callId = 2L,
            characterName = "민준",
            aiSummary = "출근 준비와 아침 일정 이야기",
            startedAt = LocalDateTime.of(2026, 6, 27, 7, 31),
            sender = CallSender.AI,
            callType = CallType.DAILY,
            status = CallStatus.COMPLETED,
        ),
        CallHistory(
            callId = 3L,
            characterName = "민준",
            aiSummary = "",
            startedAt = LocalDateTime.of(2026, 6, 26, 21, 29),
            sender = CallSender.AI,
            callType = CallType.DEEP,
            status = CallStatus.MISSED,
        ),
    )

    val state: CallHistoryState = CallHistoryState(
        histories = histories.map { callHistory -> callHistory.toUiState() },
    )
}

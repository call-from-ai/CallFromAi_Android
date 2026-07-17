package kr.co.call.domain.model.home

import java.time.LocalDateTime

data class CallHistory(
    val callId: Long,
    val characterName: String,
    val aiSummary: String,
    val startedAt: LocalDateTime,
    val sender: CallSender,
    val callType: CallType,
    val status: CallStatus,
)

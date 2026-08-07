package kr.co.call.data.mapper

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import kr.co.call.domain.model.call.ActiveCharacter
import kr.co.call.domain.model.call.CallConnectionInfo
import kr.co.call.domain.model.call.CallEndInfo
import kr.co.call.domain.model.call.CallListItem
import kr.co.call.domain.model.call.CallSender
import kr.co.call.domain.model.call.CallStatus
import kr.co.call.domain.model.call.IncomingCall
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.domain.model.home.CallRecordStatus
import kr.co.call.domain.model.home.CallTranscript
import kr.co.call.network.dto.call.CallCharacterDetailDto
import kr.co.call.network.dto.call.CallConnectionResultDto
import kr.co.call.network.dto.call.CallDetailResultDto
import kr.co.call.network.dto.call.CallEndResultDto
import kr.co.call.network.dto.call.CallListItemDto
import kr.co.call.network.dto.call.CallScriptLineDto
import kr.co.call.network.dto.call.CallScriptResultDto
import kr.co.call.network.dto.call.IncomingCallResultDto

internal fun CallListItemDto.toDomain(): CallListItem =
    CallListItem(
        callId = callId,
        characterName = characterName,
        sender = sender.toCallSender(),
        aiSummary = aiSummary.orEmpty(),
        createdAt = createdAt.toLocalDateTime(),
        status = status.toCallStatus(),
    )

internal fun CallDetailResultDto.toDomain(callId: Long): CallInfo =
    CallInfo(
        callId = callId,
        title = aiSummary.orEmpty(),
        calledAt = createdAt.toLocalDateTime(),
        characterName = characterName,
        recordingUrl = audioUrl,
        durationMillis = 0L,
        summaryStatus = summaryStatus.toCallRecordStatus(),
        recordingStatus = recordingStatus.toCallRecordStatus(),
    )

internal fun CallScriptResultDto.toDomain(): List<CallTranscript> =
    lines.map(CallScriptLineDto::toDomain)

private fun CallScriptLineDto.toDomain(): CallTranscript =
    CallTranscript(
        content = content,
        speaker = speaker.toTranscriptSpeaker(),
        createdAt = createdAt.toLocalDateTime(),
    )

internal fun CallCharacterDetailDto.toDomain(): ActiveCharacter =
    ActiveCharacter(
        characterId = characterId,
        name = name,
        imageUrl = imageUrl,
    )

internal fun IncomingCallResultDto.toDomain(): IncomingCall =
    IncomingCall(
        callId = callId,
        characterId = characterId,
        characterName = characterName,
        characterImageUrl = characterImage,
        chatRoomId = null, // 착신 조회 응답에는 채팅방 식별자가 없어 FCM 연결 전까지 null 처리
    )

internal fun CallConnectionResultDto.toDomain(): CallConnectionInfo =
    CallConnectionInfo(
        callId = callId,
        callStatus = callStatus.toCallStatus(),
        wsTicket = wsTicket,
    )

internal fun CallEndResultDto.toDomain(): CallEndInfo =
    CallEndInfo(
        callId = callId,
        callTime = callTime,
        endedAt = endedAt.toLocalDateTime(),
    )

private fun String.toCallSender(): CallSender =
    when (uppercase()) {
        "USER" -> CallSender.USER
        "AI" -> CallSender.AI
        else -> CallSender.UNKNOWN
    }

private fun String.toCallStatus(): CallStatus =
    when (uppercase()) {
        "RINGING" -> CallStatus.RINGING
        "PENDING" -> CallStatus.PENDING
        "IN_PROGRESS" -> CallStatus.IN_PROGRESS
        "COMPLETED" -> CallStatus.COMPLETED
        "CANCELED", "CANCELLED" -> CallStatus.CANCELED
        "MISSED" -> CallStatus.MISSED
        "REJECTED" -> CallStatus.REJECTED
        else -> CallStatus.UNKNOWN
    }

private fun String.toCallRecordStatus(): CallRecordStatus =
    when (uppercase()) {
        "NONE" -> CallRecordStatus.NONE
        "PROCESSING" -> CallRecordStatus.PROCESSING
        "READY" -> CallRecordStatus.READY
        "FAILED" -> CallRecordStatus.FAILED
        else -> CallRecordStatus.NONE
    }

private fun String.toTranscriptSpeaker(): CallTranscript.Speaker =
    when (uppercase()) {
        "USER" -> CallTranscript.Speaker.USER
        "AI" -> CallTranscript.Speaker.AI
        else -> error("지원하지 않는 통화 화자입니다: $this")
    }

/**
 * Offset이 포함된 서버 시각을 기기 시간대로 변환
 */
private fun String.toLocalDateTime(): LocalDateTime =
    runCatching {
        OffsetDateTime.parse(this)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
    }.getOrElse {
        LocalDateTime.parse(this)
    }

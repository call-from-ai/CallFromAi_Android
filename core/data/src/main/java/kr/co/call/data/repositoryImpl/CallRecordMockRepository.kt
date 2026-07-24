package kr.co.call.data.repositoryImpl

import java.time.LocalDateTime
import javax.inject.Inject
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.domain.model.home.CallTranscript
import kr.co.call.domain.repository.CallRecordRepository

class CallRecordMockRepository @Inject constructor() : CallRecordRepository {

    override suspend fun getCallInfo(callId: Long): Result<CallInfo> {
        if (callId !in 1L..20L) {
            return callNotFound()
        }

        return Result.success(
            CallInfo(
                callId = callId,
                title = "출근 준비와 아침 일정 이야기",
                calledAt = LocalDateTime.of(2026, 6, 27, 7, 31),
                characterName = if (callId % 2L == 1L) "민준" else "동휘",
                recordingUrl =
                    "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                durationMillis = 0L,
            ),
        )
    }

    override suspend fun getCallScript(
        callId: Long,
    ): Result<List<CallTranscript>> {
        if (callId !in 1L..20L) {
            return callNotFound()
        }

        return Result.success(
            listOf(
                CallTranscript(
                    content = "여보세요",
                    speaker = CallTranscript.Speaker.USER,
                ),
                CallTranscript(
                    content = "잘 일어났어? 목소리 아직 잠긴 것 같은데.",
                    speaker = CallTranscript.Speaker.AI,
                ),
                CallTranscript(
                    content = "응 방금 일어나 준비하고 있었어",
                    speaker = CallTranscript.Speaker.USER,
                ),
                CallTranscript(
                    content = "오늘 출근이지? 몸은 좀 괜찮아? 피곤해 보여.",
                    speaker = CallTranscript.Speaker.AI,
                ),
            ),
        )
    }

    private fun <T> callNotFound(): Result<T> =
        Result.failure(
            IllegalArgumentException("통화 기록을 찾을 수 없습니다."),
        )
}

package kr.co.call.domain.model.home

import java.time.LocalDateTime

data class CallInfo(
    val callId: Long,
    val title: String,
    val calledAt: LocalDateTime,
    val characterName: String,
    val recordingUrl: String?,
    val durationMillis: Long,
    val summaryStatus: CallRecordStatus,
    val recordingStatus: CallRecordStatus,
)

/**
 * 통화 종료 후 요약(summaryStatus)·녹음(recordingStatus) 산출물의 생성 상태.
 * 둘 다 통화 종료 후 비동기로 만들어지므로 재조회가 필요할 수 있음
 */
enum class CallRecordStatus {
    NONE, // 산출물 없음 (연결 실패·즉시 종료 등)
    PROCESSING, // 생성 중. 재조회가 의미 있는 유일한 상태
    READY, // 준비 완료
    FAILED, // 생성 실패
}

data class CallTranscript(
    val content: String,
    val speaker: Speaker,
    val createdAt: LocalDateTime,
) {
    enum class Speaker {
        USER,
        AI,
    }
}

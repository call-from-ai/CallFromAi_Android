package kr.co.call.domain.repository

import kr.co.call.domain.model.call.CallListItem
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.domain.model.home.CallTranscript

interface CallRecordRepository {

    // 종료된 최근 통화 목록 조회
    suspend fun getCalls(): Result<List<CallListItem>>

    // 통화 기록 전문 가져오기
    // wait=true: 요약·녹음 산출물 생성 완료까지 응답 대기, 통화 종료 시에만 호출
    suspend fun getCallInfo(callId: Long, wait: Boolean = false): Result<CallInfo>

    // 통화 내용 전문 불러오기
    suspend fun getCallScript(callId: Long): Result<List<CallTranscript>>
}

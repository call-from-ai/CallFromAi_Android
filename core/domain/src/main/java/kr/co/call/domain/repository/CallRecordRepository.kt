package kr.co.call.domain.repository

import kr.co.call.domain.model.home.CallInfo
import kr.co.call.domain.model.home.CallTranscript

interface CallRecordRepository {

    // 통화 기록 전문 가져오기
    suspend fun getCallInfo(callId: Long): Result<CallInfo>

    // 통화 내용 전문 불러오기
    suspend fun getCallScript(callId: Long): Result<List<CallTranscript>>
}

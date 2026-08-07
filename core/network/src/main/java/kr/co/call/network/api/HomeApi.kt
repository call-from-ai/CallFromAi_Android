package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.home.CallDetailDto
import kr.co.call.network.dto.home.CallHistoryPageDto
import kr.co.call.network.dto.home.CallTranscriptDto
import kr.co.call.network.dto.home.HomeNotificationDto
import kr.co.call.network.dto.home.HomeSummaryDto
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

// 서버 API 경로 확정 전 임시 경로 사용
interface HomeApi {

    // 통화 기록 조회
    @GET("calls")
    suspend fun getCallHistories(): ApiResponse<CallHistoryPageDto>

    // 관계 요약 조회
    @GET("relationships/current")
    suspend fun getSummary(): ApiResponse<HomeSummaryDto>

    // 지난 알림 목록 조회
    @GET("notifications")
    suspend fun getNotifications(): ApiResponse<List<HomeNotificationDto>>

    // 전체 알림 읽음 처리
    @PATCH("notifications/read-all")
    suspend fun readAllNotifications(): ApiResponse<Unit>

    // 통화 스크립트 조회
    @GET("calls/{callId}/script")
    suspend fun getTranscript(
        @Path("callId") callId: Long,
    ): ApiResponse<CallTranscriptDto>

    // 통화 기록 상세 조회
    @GET("calls/{callId}")
    suspend fun getCallDetail(
        @Path("callId") callId: Long,
    ): ApiResponse<CallDetailDto>
}

package kr.co.call.network.api

import kr.co.call.network.dto.CallHistoryListResponseDto
import kr.co.call.network.dto.HomeSummaryResponseDto
import kr.co.call.network.dto.ReservationListResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

// 서버 API 경로 확정 전 임시 경로 사용
interface HomeApi {

    @GET("api/v1/call-reservations")
    suspend fun getReservations(): ReservationListResponseDto

    @GET("api/v1/calls")
    suspend fun getCallHistories(@Query("size") size: Int = 20): CallHistoryListResponseDto

    @GET("api/v1/home/summary")
    suspend fun getSummary(): HomeSummaryResponseDto
}

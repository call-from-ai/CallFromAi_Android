package kr.co.call.network.api

import kr.co.call.network.dto.ApiResponse
import kr.co.call.network.dto.call.CallCharacterDetailDto
import kr.co.call.network.dto.call.CallConnectionResultDto
import kr.co.call.network.dto.call.CallDetailResultDto
import kr.co.call.network.dto.call.CallEndResultDto
import kr.co.call.network.dto.call.CallListResultDto
import kr.co.call.network.dto.call.CallScriptResultDto
import kr.co.call.network.dto.call.DialCallRequestDto
import kr.co.call.network.dto.call.IncomingCallResultDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 통화 발신,착신,종료,기록 조회 API
 */
interface CallApi {

    @GET("calls")
    suspend fun getCalls(): ApiResponse<CallListResultDto>

    @GET("calls/{callId}")
    suspend fun getCall(
        @Path("callId") callId: Long,
        @Query("wait") wait: Boolean = false,
    ): ApiResponse<CallDetailResultDto>

    @GET("calls/{callId}/script")
    suspend fun getCallScript(
        @Path("callId") callId: Long,
    ): ApiResponse<CallScriptResultDto>

    @GET("calls/incoming")
    suspend fun getIncomingCall(): ApiResponse<IncomingCallResultDto>

    // characterId로 캐릭터 상세 조회. 통화 화면에 필요한 이름·사진만 사용
    @GET("characters/{characterId}")
    suspend fun getCharacter(
        @Path("characterId") characterId: Long,
    ): ApiResponse<CallCharacterDetailDto>

    @PATCH("calls/{callId}/reject")
    suspend fun rejectCall(
        @Path("callId") callId: Long,
    ): ApiResponse<Any>

    @PATCH("calls/{callId}/end")
    suspend fun endCall(
        @Path("callId") callId: Long,
    ): ApiResponse<CallEndResultDto>

    @PATCH("calls/{callId}/accept")
    suspend fun acceptCall(
        @Path("callId") callId: Long,
    ): ApiResponse<CallConnectionResultDto>

    @POST("calls")
    suspend fun dialCall(
        @Body request: DialCallRequestDto,
    ): ApiResponse<CallConnectionResultDto>
}

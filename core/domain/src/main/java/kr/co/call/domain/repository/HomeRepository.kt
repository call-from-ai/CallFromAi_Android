package kr.co.call.domain.repository

import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.CallInfo
import kr.co.call.domain.model.home.CallReservations
import kr.co.call.domain.model.home.CallTranscript
import kr.co.call.domain.model.home.HomeCharacter
import kr.co.call.domain.model.home.HomeSummary
import java.time.LocalDateTime

interface HomeRepository {
    suspend fun getCharacters(): Result<List<HomeCharacter>>

    suspend fun getReservations(): Result<CallReservations>

    suspend fun getCallHistories(): Result<List<CallHistory>>

    suspend fun getCallInfo(callId: Long): Result<CallInfo>

    suspend fun getCallScript(callId: Long): Result<List<CallTranscript>>

    suspend fun getSummary(): Result<HomeSummary>

    suspend fun changeReservationTime(
        reservationId: Long,
        scheduledAt: LocalDateTime,
    ): Result<CallReservations>

    suspend fun changeMainCharacter(
        characterId: Long,
    ): Result<List<HomeCharacter>>

    suspend fun startCall(
        characterId: Long,
    ): Result<Unit>
}

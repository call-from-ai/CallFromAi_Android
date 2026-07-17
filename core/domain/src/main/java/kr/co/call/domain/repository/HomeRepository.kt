package kr.co.call.domain.repository

import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.CallReservations
import kr.co.call.domain.model.home.HomeSummary

interface HomeRepository {
    suspend fun getReservations(): Result<CallReservations>

    suspend fun getCallHistories(): Result<List<CallHistory>>

    suspend fun getSummary(characterId: Long): Result<HomeSummary>
}

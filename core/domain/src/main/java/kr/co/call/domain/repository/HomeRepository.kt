package kr.co.call.domain.repository

import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.HomeModel

interface HomeRepository {
    suspend fun getReservations(): Result<HomeModel.Reservations>

    suspend fun getCallHistories(): Result<List<CallHistory>>

    suspend fun getSummary(characterId: Long): Result<HomeModel.Summary>
}

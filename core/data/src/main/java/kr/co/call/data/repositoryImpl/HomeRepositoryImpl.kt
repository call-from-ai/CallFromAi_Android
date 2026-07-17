package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kr.co.call.data.mapper.toDomain
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.HomeModel
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.network.api.HomeApi

class HomeRepositoryImpl @Inject constructor(
    private val homeApi: HomeApi,
) : HomeRepository {

    override suspend fun getReservations(): Result<HomeModel.Reservations> =
        runCatching {
            homeApi.getReservations().result.toDomain()
        }

    override suspend fun getCallHistories(): Result<List<CallHistory>> =
        runCatching {
            homeApi.getCallHistories(size = CALL_HISTORY_SIZE).result.content.map { it.toDomain() }
        }

    override suspend fun getSummary(characterId: Long): Result<HomeModel.Summary> =
        runCatching {
            homeApi.getSummary(characterId).result.toDomain(characterId)
        }

    private companion object {
        const val CALL_HISTORY_SIZE = 20
    }
}

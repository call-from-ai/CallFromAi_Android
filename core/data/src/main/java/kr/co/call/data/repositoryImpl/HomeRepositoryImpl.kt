package kr.co.call.data.repositoryImpl

import java.time.LocalDateTime
import javax.inject.Inject
import kr.co.call.data.mapper.toDomain
import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.CallReservations
import kr.co.call.domain.model.home.HomeCharacter
import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.network.api.HomeApi

class HomeRepositoryImpl @Inject constructor(
    private val homeApi: HomeApi,
) : HomeRepository {

    override suspend fun getCharacters(): Result<List<HomeCharacter>> =
        unsupportedApi()

    override suspend fun getReservations(): Result<CallReservations> =
        runCatching {
            homeApi.getReservations().result.toDomain()
        }

    override suspend fun getCallHistories(): Result<List<CallHistory>> =
        runCatching {
            homeApi.getCallHistories().result.content.map { callHistory ->
                callHistory.toDomain()
            }
        }

    override suspend fun getSummary(): Result<HomeSummary> =
        runCatching {
            homeApi.getSummary().result.toDomain()
        }

    override suspend fun changeReservationTime(
        reservationId: Long,
        scheduledAt: LocalDateTime,
    ): Result<CallReservations> = unsupportedApi()

    override suspend fun changeMainCharacter(
        characterId: Long,
    ): Result<List<HomeCharacter>> = unsupportedApi()

    override suspend fun startCall(
        characterId: Long,
    ): Result<Unit> = unsupportedApi()

    private fun <T> unsupportedApi(): Result<T> =
        Result.failure(
            UnsupportedOperationException("서버 API가 아직 연결되지 않았습니다."),
        )
}

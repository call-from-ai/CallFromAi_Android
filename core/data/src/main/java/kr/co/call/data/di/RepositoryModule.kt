package kr.co.call.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kr.co.call.data.repositoryImpl.HomeRepositoryImpl
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.network.api.HomeApi
import kr.co.call.network.dto.CallHistoryDto
import kr.co.call.network.dto.CallHistoryListResponseDto
import kr.co.call.network.dto.CallHistoryPageDto
import kr.co.call.network.dto.HomeSummaryDto
import kr.co.call.network.dto.HomeSummaryResponseDto
import kr.co.call.network.dto.ReservationDto
import kr.co.call.network.dto.ReservationListDto
import kr.co.call.network.dto.ReservationListResponseDto

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl,
    ): HomeRepository = homeRepositoryImpl

    // 실제 HomeApi 연결 전 사용하는 Mock 데이터
    @Provides
    @Singleton
    fun provideHomeApi(): HomeApi =
        object : HomeApi {
            override suspend fun getReservations(): ReservationListResponseDto =
                ReservationListResponseDto(
                    isSuccess = true,
                    code = "COMMON200",
                    message = "요청에 성공하였습니다.",
                    result = ReservationListDto(
                        content = listOf(
                            ReservationDto(
                                callReservationId = 3L,
                                characterId = 2L,
                                firstName = "민준",
                                imageUrl = null,
                                scheduledAt = "2026-06-30T21:00:00",
                                status = "SCHEDULED",
                            ),
                        ),
                        count = 1,
                    ),
                )

            override suspend fun getCallHistories(size: Int): CallHistoryListResponseDto {
                val histories = List(size.coerceAtMost(MAX_CALL_HISTORY_SIZE)) { index ->
                    CallHistoryDto(
                        callId = (index + 1).toLong(),
                        characterName = if (index % 2 == 0) "민준" else "동휘",
                        sender = if (index % 2 == 0) "USER" else "AI",
                        aiSummary = if (index % 2 == 0) {
                            "오늘 하루와 퇴근 후 일상 이야기"
                        } else {
                            "몸살 감기 기운과 걱정해주는 이야기"
                        },
                        startedAt = "2026-06-${(28 - index).coerceAtLeast(1).toString().padStart(2, '0')}T23:02:00",
                        callType = if (index % 2 == 0) "DAILY" else "DEEP",
                        status = if (index % 4 == 3) "MISSED" else "COMPLETED",
                    )
                }

                return CallHistoryListResponseDto(
                    isSuccess = true,
                    code = "COMMON200",
                    message = "요청에 성공하였습니다.",
                    result = CallHistoryPageDto(
                        content = histories,
                        hasNext = false,
                    ),
                )
            }

            override suspend fun getSummary(characterId: Long): HomeSummaryResponseDto =
                HomeSummaryResponseDto(
                    isSuccess = true,
                    code = "COMMON200",
                    message = "요청에 성공하였습니다.",
                    result = HomeSummaryDto(
                        firstName = "수현",
                        relationshipDays = 30,
                        totalCallCount = 24,
                        callStreakDays = 12,
                    ),
                )
        }

    private const val MAX_CALL_HISTORY_SIZE = 20
}

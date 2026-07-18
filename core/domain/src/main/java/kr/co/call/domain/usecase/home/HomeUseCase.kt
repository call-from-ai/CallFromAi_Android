package kr.co.call.domain.usecase.home

import kr.co.call.domain.model.home.HomeOverview
import kr.co.call.domain.repository.HomeRepository

class HomeUseCase(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): HomeOverview {
        val summary = homeRepository.getSummary().getOrThrow()
        val reservations = homeRepository.getReservations().getOrThrow()
        val callHistories = homeRepository.getCallHistories().getOrThrow()

        return HomeOverview(
            summary = summary,
            reservations = reservations,
            callHistories = callHistories,
        )
    }

}

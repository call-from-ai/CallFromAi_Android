package kr.co.call.domain.usecase.home

import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.HomeCharacter
import kr.co.call.domain.model.home.HomeSummary
import kr.co.call.domain.repository.HomeRepository
import javax.inject.Inject

class HomeUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend fun getSummary(): HomeSummary =
        homeRepository.getSummary().getOrThrow()

    suspend fun getCallHistories(): List<CallHistory> =
        homeRepository.getCallHistories().getOrThrow()

    suspend fun getCharacters(): List<HomeCharacter> =
        homeRepository.getCharacters().getOrThrow()

    suspend fun activateCharacter(characterId: Long) =
        homeRepository.activateCharacter(characterId).getOrThrow()

    suspend fun startCall(characterId: Long) =
        homeRepository.startCall(characterId).getOrThrow()
}

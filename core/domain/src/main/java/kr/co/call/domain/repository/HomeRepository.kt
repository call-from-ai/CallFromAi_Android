package kr.co.call.domain.repository

import kr.co.call.domain.model.home.CallHistory
import kr.co.call.domain.model.home.HomeCharacter
import kr.co.call.domain.model.home.HomeSummary

interface HomeRepository {
    suspend fun getCharacters(): Result<List<HomeCharacter>>

    suspend fun getCallHistories(): Result<List<CallHistory>>

    suspend fun getSummary(): Result<HomeSummary>

    suspend fun activateCharacter(
        characterId: Long,
    ): Result<Unit>

}

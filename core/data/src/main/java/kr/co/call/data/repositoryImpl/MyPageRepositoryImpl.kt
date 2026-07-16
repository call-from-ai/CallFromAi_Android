package kr.co.call.data.repositoryImpl

import kotlinx.coroutines.delay
import kr.co.call.domain.model.mypage.MyPageProfile
import kr.co.call.domain.repository.MyPageRepository
import javax.inject.Inject

// TODO: API 연동 전 임시 구현체
class MyPageRepositoryImpl @Inject constructor() : MyPageRepository {

    override suspend fun getMyProfile(): MyPageProfile {
        delay(500)
        return MyPageProfile(
            profileImageUrl = "",
            nickname = "김수현",
            tier = "Basic",
            remainingTicketCount = 18,
            appVersion = "1.0.0",
        )
    }

    override suspend fun logout() {
        delay(300)
    }

    override suspend fun deleteAccount() {
        delay(300)
    }
}
package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kotlinx.coroutines.delay
import kr.co.call.data.push.PushTokenManager
import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.model.mypage.MyPageProfile
import kr.co.call.domain.repository.MyPageRepository

// TODO: 프로필 API 연동 전 임시 구현체 (logout/delete 는 토큰 정리 포함)
class MyPageRepositoryImpl @Inject constructor(
    private val pushTokenManager: PushTokenManager,
    private val tokenDataStore: TokenDataStore,
) : MyPageRepository {

    override suspend fun getMyProfile(): Result<MyPageProfile> {
        delay(500)
        return runCatching {
            MyPageProfile(
                profileImageUrl = "",
                nickname = "김수현",
                tier = "Basic",
                remainingTicketCount = 18,
                appVersion = "1.0.0",
            )
        }
    }

    /**
     * 로그아웃: FCM 서버 해제 > 로컬 FCM 토큰 삭제 > JWT 삭제.
     */
    override suspend fun logout() {
        pushTokenManager.unregisterCurrentDevice()
        tokenDataStore.clearTokens()
    }

    /**
     * 회원 탈퇴: 푸시 토큰/JWT 정리 (서버 탈퇴 API 연동 전 로컬 정리)
     */
    override suspend fun deleteAccount() {
        pushTokenManager.unregisterCurrentDevice()
        tokenDataStore.clearTokens()
    }
}

package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kr.co.call.data.push.PushTokenManager
import kr.co.call.data.util.safeApiResultUnit
import kr.co.call.data.util.safeEmptyBodyApiResult
import kr.co.call.datastore.TokenDataStore
import kr.co.call.data.util.safeApiResultUnit
import kr.co.call.data.util.toAppResult
import kr.co.call.domain.model.mypage.MyPageProfile
import kr.co.call.domain.repository.MyPageRepository
import kr.co.call.network.api.MyPageApi
import kr.co.call.network.util.ErrorResponseParser
import timber.log.Timber

class MyPageRepositoryImpl @Inject constructor(
    private val myPageApi: MyPageApi,
    private val tokenDataStore: TokenDataStore,
    private val errorResponseParser: ErrorResponseParser,
    private val pushTokenManager: PushTokenManager,
) : MyPageRepository {

    override suspend fun getMyProfile(): Result<MyPageProfile> {
        delay(500)
        return Result.success(
            MyPageProfile(
                profileImageUrl = "",
                nickname = "김수현",
                tier = "Basic",
                remainingTicketCount = 18,
                appVersion = "1.0.0",
            ),
        )
    }

    /**
     * 로그아웃: FCM 서버 해제 > 로컬 FCM 토큰 삭제 > JWT 삭제.
     */
    override suspend fun logout(): Result<Unit> =
        safeEmptyBodyApiResult(errorResponseParser) {
            myPageApi.logout()
        }.mapCatching {
            pushTokenManager.unregisterCurrentDevice()
            tokenDataStore.clearTokens()
        }.toAppResult()


    /**
     * 회원 탈퇴: 푸시 토큰/JWT 정리 (서버 탈퇴 API 연동 전 로컬 정리)
     */
    override suspend fun deleteAccount(): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            myPageApi.deleteAccount()
        }.onSuccess {
            try {
                pushTokenManager.unregisterCurrentDevice()
                tokenDataStore.clearTokens()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                Timber.w(error, "회원 탈퇴 후 토큰 삭제 실패")
            }
        }

}

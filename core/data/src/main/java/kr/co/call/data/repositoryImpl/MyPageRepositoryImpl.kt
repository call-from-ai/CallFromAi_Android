package kr.co.call.data.repositoryImpl

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kr.co.call.data.util.runRepositoryCatching
import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.model.mypage.MyPageProfile
import kr.co.call.domain.repository.MyPageRepository
import kr.co.call.network.api.MyPageApi
import kr.co.call.network.util.ErrorResponseParser
import kr.co.call.network.util.safeApiCallUnit
import timber.log.Timber
import javax.inject.Inject

class MyPageRepositoryImpl @Inject constructor(
    private val myPageApi: MyPageApi,
    private val tokenDataStore: TokenDataStore,
    private val errorResponseParser: ErrorResponseParser
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

    override suspend fun logout():Result<Unit> =
        runRepositoryCatching {
            safeApiCallUnit(errorResponseParser){
                myPageApi.logout()
            }
            //서버 로그아웃 성공 후 기기 토큰 삭제
            tokenDataStore.clearTokens()
        }
    override suspend fun deleteAccount(): Result<Unit> =
        runRepositoryCatching {
            //토큰이 남아 있을 때 탈퇴 api를 먼저 호출
            safeApiCallUnit(errorResponseParser){
                myPageApi.deleteAccount()
            }
            runCatching{
                tokenDataStore.clearTokens()
            }.onFailure { error->
                if (error is CancellationException) throw error
                Timber.w(error, "회원 탈퇴 후 토큰 삭제 실패")
            }
            Unit
        }

}

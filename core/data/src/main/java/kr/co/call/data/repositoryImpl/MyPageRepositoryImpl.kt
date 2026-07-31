package kr.co.call.data.repositoryImpl

import kotlinx.coroutines.delay
import kr.co.call.data.util.runRepositoryCatching
import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.model.mypage.MyPageProfile
import kr.co.call.domain.repository.MyPageRepository
import kr.co.call.network.api.MyPageApi
import kr.co.call.network.util.ErrorResponseParser
import kr.co.call.network.util.safeApiCallUnit
import kr.co.call.network.util.safeEmptyApiCall
import javax.inject.Inject

// TODO: API 연동 전 임시 구현체
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
            safeEmptyApiCall(errorResponseParser){
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
            //서버 탈퇴 성공 후 기기의 토큰 삭제
            tokenDataStore.clearTokens()
        }

}
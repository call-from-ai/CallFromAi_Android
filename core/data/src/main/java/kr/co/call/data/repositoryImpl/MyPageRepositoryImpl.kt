package kr.co.call.data.repositoryImpl

import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kr.co.call.data.mapper.toDomain
import kr.co.call.data.mapper.toRequestDto
import kr.co.call.data.push.PushTokenManager
import kr.co.call.data.util.safeApiResult
import kr.co.call.data.util.safeApiResultUnit
import kr.co.call.data.util.toAppResult
import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.model.mypage.MemberProfileUpdate
import kr.co.call.domain.model.mypage.MyPageProfile
import kr.co.call.domain.model.mypage.NotificationSetting
import kr.co.call.domain.repository.MyPageRepository
import kr.co.call.network.api.CharacterApi
import kr.co.call.network.api.MyPageApi
import kr.co.call.network.api.RelationshipApi
import kr.co.call.network.dto.mypage.ContactPreferenceUpdateRequestDto
import kr.co.call.network.dto.mypage.DoNotDisturbUpdateRequestDto
import kr.co.call.network.dto.mypage.NotificationSettingUpdateRequestDto
import kr.co.call.network.util.ErrorResponseParser
import timber.log.Timber

class MyPageRepositoryImpl @Inject constructor(
    private val myPageApi: MyPageApi,
    private val characterApi: CharacterApi,
    private val relationshipApi: RelationshipApi,
    private val tokenDataStore: TokenDataStore,
    private val errorResponseParser: ErrorResponseParser,
    private val pushTokenManager: PushTokenManager,
) : MyPageRepository {

    override suspend fun getMyProfile(): Result<MyPageProfile> =
        safeApiResult(errorResponseParser) {
            myPageApi.getMyInfo()
        }.map { it.toDomain() }

    override suspend fun updateMyProfile(update: MemberProfileUpdate): Result<MyPageProfile> =
        safeApiResult(errorResponseParser) {
            myPageApi.updateMember(update.toRequestDto())
        }.map { it.toDomain() }

    override suspend fun getPreferTime(): Result<String?> =
        safeApiResult(errorResponseParser) {
            characterApi.getActiveCharacter()
        }.map { it.preferTime }

    override suspend fun updatePreferTime(preferTime: String): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            relationshipApi.updateContactPreference(
                ContactPreferenceUpdateRequestDto(preferTime = preferTime),
            )
        }

    override suspend fun getNotificationSetting(): Result<NotificationSetting> =
        safeApiResult(errorResponseParser) {
            myPageApi.getNotificationSettings()
        }.map { it.toDomain() }

    override suspend fun updateNotificationToggles(
        allNotificationEnabled: Boolean?,
        nightCallAllowed: Boolean?,
    ): Result<NotificationSetting> =
        safeApiResult(errorResponseParser) {
            myPageApi.updateNotificationSettings(
                NotificationSettingUpdateRequestDto(
                    allNotificationEnabled = allNotificationEnabled,
                    nightCallAllowed = nightCallAllowed,
                ),
            )
        }.map { it.toDomain() }

    override suspend fun updateDoNotDisturb(
        startTime: String,
        endTime: String,
    ): Result<NotificationSetting> =
        safeApiResult(errorResponseParser) {
            myPageApi.updateDoNotDisturb(
                DoNotDisturbUpdateRequestDto(
                    startTime = startTime,
                    endTime = endTime,
                ),
            )
        }.map { it.toDomain() }

    /**
     * 로그아웃: FCM 서버 해제 > 로컬 FCM 토큰 삭제 > JWT 삭제.
     */
    override suspend fun logout(): Result<Unit> =
        safeApiResultUnit(errorResponseParser) {
            myPageApi.logout()
        }.mapCatching {
            pushTokenManager.unregisterCurrentDevice()
            tokenDataStore.clearTokens()
        }.toAppResult()

    /**
     * 회원 탈퇴: 서버 탈퇴 후 푸시 토큰/JWT 정리.
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

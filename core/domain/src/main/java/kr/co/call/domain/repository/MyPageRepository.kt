package kr.co.call.domain.repository

import kr.co.call.domain.model.login.AuthRequirements
import kr.co.call.domain.model.mypage.MemberProfileUpdate
import kr.co.call.domain.model.mypage.MyPageProfile
import kr.co.call.domain.model.mypage.NotificationSetting

interface MyPageRepository {
    suspend fun getMyProfile(): Result<MyPageProfile>
    suspend fun getAuthRequirements(): Result<AuthRequirements>

    suspend fun updateMyProfile(update: MemberProfileUpdate): Result<MyPageProfile>

    suspend fun getPreferTime(): Result<String?>

    suspend fun updatePreferTime(preferTime: String): Result<Unit>

    suspend fun getNotificationSetting(): Result<NotificationSetting>

    suspend fun updateNotificationToggles(
        allNotificationEnabled: Boolean? = null,
        nightCallAllowed: Boolean? = null,
    ): Result<NotificationSetting>

    suspend fun updateDoNotDisturb(
        startTime: String,
        endTime: String,
    ): Result<NotificationSetting>

    suspend fun deleteDoNotDisturb(): Result<NotificationSetting>

    suspend fun logout(): Result<Unit>

    suspend fun deleteAccount(): Result<Unit>
}

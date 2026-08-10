package kr.co.call.data.mapper

import kr.co.call.domain.model.NameLimits
import kr.co.call.domain.model.mypage.MemberProfileUpdate
import kr.co.call.domain.model.mypage.MyPageProfile
import kr.co.call.domain.model.mypage.NotificationSetting
import kr.co.call.network.dto.mypage.MemberResponseDto
import kr.co.call.network.dto.mypage.NotificationSettingDto
import kr.co.call.network.dto.onboarding.UpdateMemberRequestDto

internal fun MemberResponseDto.toDomain(
    appVersion: String = "",
): MyPageProfile {
    val last = lastName.orEmpty()
    val first = firstName.orEmpty()
    return MyPageProfile(
        profileImageUrl = profilePhotoUrl.orEmpty(),
        nickname = last + first,
        lastName = last,
        firstName = first,
        gender = gender,
        birth = birth,
        mbti = mbti,
        job = job,
        tier = "Basic",
        remainingTicketCount = callTicketBalance,
        appVersion = appVersion,
    )
}

internal fun MemberProfileUpdate.toRequestDto(): UpdateMemberRequestDto =
    UpdateMemberRequestDto(
        lastName = lastName?.trim()?.take(NameLimits.LAST_NAME_MAX),
        firstName = firstName?.trim()?.take(NameLimits.FIRST_NAME_MAX),
        imageUrl = imageUrl,
        gender = gender,
        birth = birth,
        mbti = mbti,
        job = job,
    )

internal fun NotificationSettingDto.toDomain(): NotificationSetting =
    NotificationSetting(
        allNotificationEnabled = allNotificationEnabled,
        nightCallAllowed = nightCallAllowed,
        doNotDisturbStart = doNotDisturbStart,
        doNotDisturbEnd = doNotDisturbEnd,
    )


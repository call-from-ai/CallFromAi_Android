package kr.co.call.data.mapper

import kr.co.call.domain.model.mypage.MemberProfileUpdate
import kr.co.call.domain.model.mypage.MyPageProfile
import kr.co.call.network.dto.mypage.MemberResponseDto
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
        lastName = lastName,
        firstName = firstName,
        imageUrl = imageUrl,
        gender = gender,
        birth = birth,
        mbti = mbti,
        job = job,
    )

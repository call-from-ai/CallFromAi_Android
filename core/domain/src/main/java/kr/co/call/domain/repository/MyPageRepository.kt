package kr.co.call.domain.repository

import kr.co.call.domain.model.mypage.MemberProfileUpdate
import kr.co.call.domain.model.mypage.MyPageProfile

interface MyPageRepository {
    suspend fun getMyProfile(): Result<MyPageProfile>

    suspend fun updateMyProfile(update: MemberProfileUpdate): Result<MyPageProfile>

    suspend fun logout(): Result<Unit>

    suspend fun deleteAccount(): Result<Unit>
}

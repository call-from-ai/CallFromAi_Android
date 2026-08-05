package kr.co.call.domain.repository

import kr.co.call.domain.model.mypage.MyPageProfile

interface MyPageRepository {
    suspend fun getMyProfile(): Result<MyPageProfile>
    suspend fun getNeedsOnboarding():Result<Boolean>
    suspend fun logout(): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
}
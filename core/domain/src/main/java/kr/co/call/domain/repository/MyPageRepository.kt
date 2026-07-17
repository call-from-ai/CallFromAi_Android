package kr.co.call.domain.repository

import kr.co.call.domain.model.mypage.MyPageProfile

interface MyPageRepository {
    suspend fun getMyProfile(): Result<MyPageProfile>
    suspend fun logout()
    suspend fun deleteAccount()
}
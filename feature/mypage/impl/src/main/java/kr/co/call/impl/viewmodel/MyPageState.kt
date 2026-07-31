package kr.co.call.impl.viewmodel

import kr.co.call.domain.util.LoadStatus

data class MyPageState(
    val profileImageUrl: String = "",
    val nickname: String = "",
    val tier: String = "",
    val remainingTicketCount: Int = 0,
    val appVersion: String = "",
    //프로필 조회 상태
    val loadStatus: LoadStatus = LoadStatus.Idle,
    //로그아웃, 탈퇴 상태
    val authStatus:LoadStatus=LoadStatus.Idle,
)
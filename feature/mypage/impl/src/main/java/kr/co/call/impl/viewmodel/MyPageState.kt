package kr.co.call.impl.viewmodel

import kr.co.call.domain.util.LoadStatus

data class MyPageState(
    val profileImageUrl: String = "",
    val nickname: String = "",
    val tier: String = "",
    val remainingTicketCount: Int = 0,
    val appVersion: String = "",
    val loadStatus: LoadStatus = LoadStatus.Idle,
)
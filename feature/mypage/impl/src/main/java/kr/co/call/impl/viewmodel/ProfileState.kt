package kr.co.call.impl.viewmodel

import kr.co.call.domain.util.LoadStatus

data class ProfileState(
    val profileImageUrl: String = "",
    val nickname: String = "",
    val isAllNotificationEnabled: Boolean = true,
    val isLateNightCallAllowed: Boolean = true,
    val DisturbTimeText: String = "09-12시", // <- 연동 전 임시로만!
    val loadStatus: LoadStatus = LoadStatus.Idle,
)
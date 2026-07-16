package kr.co.call.callfromai.util

import androidx.navigation3.runtime.NavKey
import kr.co.call.api.ChattingNavKey
import kr.co.call.api.HomeNavKey
import kr.co.call.api.MyPageNavKey
import kr.co.call.api.ProfileNavKey
import kr.co.call.callfromai.ui.MainTab

fun MainTab.toNavKey(): NavKey = when (this) {
    MainTab.HOME -> HomeNavKey
    MainTab.CHATTING -> ChattingNavKey
    MainTab.MYPAGE -> MyPageNavKey
}

fun NavKey.toMainTab(): MainTab = when (this) {
    is ChattingNavKey -> MainTab.CHATTING
    is MyPageNavKey -> MainTab.MYPAGE
    is ProfileNavKey -> MainTab.MYPAGE
    is ProfileNavKey -> MainTab.MYPAGE
    else -> MainTab.HOME
}

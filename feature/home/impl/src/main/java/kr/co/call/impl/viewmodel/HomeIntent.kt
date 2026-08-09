package kr.co.call.impl.viewmodel

import kr.co.call.impl.tab.HomeHistoryTab

sealed interface HomeIntent {

    // 통화 기록 및 알림 관련
    sealed interface History : HomeIntent {
        data class SelectTab(
            val tab: HomeHistoryTab,
        ) : History

        data object ClickNotificationShortcut : History

        data class ClickRecord(
            val callId: Long,
        ) : History
    }

    // 통화 관련
    sealed interface Call : HomeIntent {
        data object ClickMain : Call

        data class ClickNotification(
            val characterId: Long,
            val characterName: String,
        ) : Call

        data object Confirm : Call
    }

    // 캐릭터 관련
    sealed interface Character : HomeIntent {
        data object ClickChange : Character

        data class Select(
            val characterId: Long,
            val characterName: String,
        ) : Character

        data object ConfirmChange : Character

        data object ClickAdd : Character
    }

    data object DismissDialog : HomeIntent

    // 홈 화면 (재)진입 시 최신 데이터로 갱신
    data object OnResume : HomeIntent
}

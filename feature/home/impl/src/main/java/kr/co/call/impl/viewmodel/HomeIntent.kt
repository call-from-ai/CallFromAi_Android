package kr.co.call.impl.viewmodel

import java.time.LocalDate
import java.time.LocalTime
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
            val characterName: String,
        ) : Call

        data object Confirm : Call
    }

    // 시간 변경
    sealed interface TimeChange : HomeIntent {
        data object Click : TimeChange

        data class SelectDate(
            val date: LocalDate,
        ) : TimeChange

        data class SelectTime(
            val time: LocalTime,
        ) : TimeChange

        data object Confirm : TimeChange
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
}

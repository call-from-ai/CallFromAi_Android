package kr.co.call.impl.viewmodel

import kr.co.call.impl.component.MainTab

sealed interface MainIntent {

    data class SelectTab(val tab: MainTab) : MainIntent
}
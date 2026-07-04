package kr.co.call.impl.viewmodel

import kr.co.call.impl.component.MainTab

sealed interface MainSideEffect {
    data class NavigateTo(val tab: MainTab) : MainSideEffect
}
package kr.co.call.impl.viewmodel

sealed interface HomeSideEffect {

    // 캐릭터 추가하기 -> 캐릭터 등록 화면으로 이동
    data object NavigateToCharacterOnboarding : HomeSideEffect

    // 전화하기 확인 -> 전화 화면으로 이동
    data object NavigateToCall : HomeSideEffect

    data class NavigateToCallRecord(
        val callId: Long,
    ) : HomeSideEffect

    data class ShowMessage(
        val message: String,
    ) : HomeSideEffect

}

package kr.co.call.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
enum class OnboardingFlowMode {
    FIRST_ONBOARDING,
    ADD_CHARACTER,
}

@Serializable
data object Onboarding1NavKey : NavKey

@Serializable
data class Onboarding2NavKey(
    val mode: OnboardingFlowMode = OnboardingFlowMode.FIRST_ONBOARDING,
    /**
     * 캐릭터 추가 등 외부에서 2로 진입할 때마다 바뀌는 값
     * 동일 백스택 복귀 시 LaunchedEffect 재실행(입력 초기화)을 막는다.
     */
    val resetToken: Long = 0L,
) : NavKey

@Serializable
data object Onboarding3NavKey: NavKey

@Serializable
data object Onboarding4NavKey: NavKey

@Serializable
data object Onboarding5NavKey: NavKey

@Serializable
data object Onboarding6NavKey: NavKey


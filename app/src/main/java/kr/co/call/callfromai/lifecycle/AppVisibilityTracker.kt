package kr.co.call.callfromai.lifecycle

import jakarta.inject.Inject
import jakarta.inject.Singleton

/**
 * MainActivity를 표시하고있는지 확인하는 클래스
 * 앱 사용중 여부에 따라 화면을 표시하기 위해 상태를 확인하는 클래스입니다.
 */
@Singleton
class AppVisibilityTracker @Inject constructor() {
    // 다른 스레드에서 접근하더라도 최근 값을 읽어야 하기 때문에 사용
    @Volatile
    var isMainActivityResumed: Boolean = false
        private set

    // 화면을 보고 있는 상태
    fun onResumed() {
        isMainActivityResumed = true
    }

    // 미사용 상태
    fun onPaused() {
        isMainActivityResumed = false
    }
}

package kr.co.call.impl.tab

/**
 * 지난알림, 통화 기록 탭
 */
enum class HomeHistoryTab(
    val title: String,
) {
    NOTIFICATION(title = "지난 알림"),
    CALL_HISTORY(title = "통화 기록"),
}
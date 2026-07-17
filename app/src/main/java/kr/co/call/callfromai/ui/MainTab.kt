package kr.co.call.callfromai.ui

import androidx.annotation.DrawableRes
import kr.co.call.designsystem.R

/**
 * 애플리케이션의 메인 네비게이션 탭을 정의하는 Enum 클래스입니다.
 *
 * @property iconRes 각 탭의 아이콘으로 사용될 드로어블 리소스 ID.
 * @property label 각 탭 하단에 표시될 텍스트 명칭.
 */
enum class MainTab(
    @DrawableRes val selectedIconRes: Int,
    @DrawableRes val unselectedIconRes: Int,
    val label: String,
) {
    HOME(R.drawable.ic_home_selected, R.drawable.ic_home_not_selected, "홈"),
    CHATTING(R.drawable.ic_chat_selected, R.drawable.ic_chat_not_selected, "채팅"),
    MYPAGE(R.drawable.ic_mypage_selected, R.drawable.ic_mypage_not_selected, "마이");

    @DrawableRes
    fun iconRes(selected: Boolean): Int =
        if (selected) selectedIconRes else unselectedIconRes
}
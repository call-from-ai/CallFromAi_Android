package kr.co.call.impl.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class PreferTime(
    val emoji: String,
    val title: String,
    val time: String?,
    val description: String,
){
    MORNING(
        emoji = "🌅",
        title = "오전 시간대",
        time = "08:00 ~ 12:00",
        description = "하루를 기분 좋게 시작할 수 있도록 아침 인사를 전해드려요.",
    ),

    DAY(
        emoji = "🏙️",
        title = "낮 시간대",
        time = "13:00 ~ 18:00",
        description = "바쁜 일상 속 잠시 쉬어갈 수 있도록 가볍게 안부를 물어봐요.",
    ),

    LATE_EVENING(
        emoji = "🌃",
        title = "늦은 오후 시간대",
        time = "19:00 ~ 24:00",
        description = "하루를 마무리하며 더 깊은 대화와 통화를 나눠요.",
    ),

    ANYTIME(
        emoji = "☺️",
        title = "언제든 좋아요",
        time = null,
        description = "시간 제한 없이 언제든 전화할게요.",
    ),
}

@Composable
fun PreferTimeItem(
    preferTime: PreferTime,
    selected: Boolean,
    onClick:()->Unit,
    modifier: Modifier =Modifier,
){

}
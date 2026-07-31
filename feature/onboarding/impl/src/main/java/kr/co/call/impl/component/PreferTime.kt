package kr.co.call.impl.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray600
import kr.co.call.designsystem.theme.MainVariant1
import kr.co.call.designsystem.theme.MainVariant2
import kr.co.call.designsystem.theme.White
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

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
fun PreferTime(
    preferTime: PreferTime,
    selected: Boolean,
    onClick:()->Unit,
    modifier: Modifier =Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(93.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            ),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) {
            MainVariant2
        } else {
            White
        },
        border = if (selected) {
            BorderStroke(
                width = 1.dp,
                color = MainVariant1,
            )
        } else {
            null
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 17.dp,
                    end = 16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier= Modifier
                    .size(20.dp)
                    .border(
                        width=3.dp,
                        color=if(selected){
                            White
                        }else {MainVariant2},
                        shape= CircleShape,
                    )
                    .background(
                        color= if(selected){
                            MainVariant1
                        }else {White},
                        shape=CircleShape,
                    ),
            ){
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = preferTime.emoji,
                        style = CallTheme.typography.titleSmallBold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = preferTime.title,
                        style = CallTheme.typography.bodyMediumMedium,
                        color = Black,
                        maxLines = 1,
                    )

                    preferTime.time?.let { time ->
                        Spacer(modifier = Modifier.width(13.dp))
                        Text(
                            text = time,
                            style = CallTheme.typography.captionBold,
                            color = MainVariant1,
                            maxLines = 1,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = preferTime.description,
                    style = CallTheme.typography.caption,
                    color = Gray600,
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 420,
    heightDp = 500,
)
@Composable
private fun PreferTimePreview() {
    var selectedPreferTime by rememberSaveable {
        mutableStateOf(PreferTime.DAY)
    }

    CallFromAiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PreferTime.entries.forEach { preferTime ->
                    PreferTime(
                        preferTime = preferTime,
                        selected = selectedPreferTime == preferTime,
                        onClick = {
                            selectedPreferTime = preferTime
                        },
                    )
                }
            }
        }
    }
}
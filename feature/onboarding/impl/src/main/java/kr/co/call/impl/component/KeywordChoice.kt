package kr.co.call.impl.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray200
import kr.co.call.designsystem.theme.MainVariant1
import kr.co.call.designsystem.theme.SubGray
import kr.co.call.designsystem.theme.White

enum class Trait(
    val keyword: String,
    val emoji: String,
    val label: String,
){
    HUMOROUS("HUMOROUS", "😃", "유머러스한"),
    PLAYFUL("PLAYFUL", "🤡", "장난기 많은"),
    AFFECTIONATE("AFFECTIONATE", "💕", "애교 많은"),
    JEALOUS("JEALOUS", "❤️‍🔥", "질투심 폭발"),
    TALKATIVE("TALKATIVE", "🗣️", "수다쟁이"),
    DAD_JOKE_LOVER("DAD_JOKE_LOVER", "😝", "아재개그 좋아하는"),
    HOMEBODY("HOMEBODY", "🛌", "집순이/집돌이"),
    TEASING("TEASING", "🏀", "놀리는 걸 좋아하는"),
    POSSESSIVE("POSSESSIVE", "📱", "집착하는"),
    TSUNDERE("TSUNDERE", "👋", "츤데레"),
    EXPRESSIVE("EXPRESSIVE", "😌", "표현을 많이 하는"),
    PET_NAME_LOVER("PET_NAME_LOVER", "👄", "애칭을 자주 쓰는"),
    EXCLUSIVE("EXCLUSIVE", "🪴", "독점욕이 있는"),
    QUIRKY("QUIRKY", "👍", "4차원 같은"),
    LAID_BACK("LAID_BACK", "🕶️", "털털한"),
    OPENLY_JEALOUS("OPENLY_JEALOUS", "📑", "질투를 숨기지 않는"),
    SHY("SHY", "🧐", "부끄러움을 많이 타는"),
    SMOOTH_TALKER("SMOOTH_TALKER", "☄\uFE0F", "능청스러운"),
    FREQUENT_CHECKER("FREQUENT_CHECKER", "💌", "연락을 자주 확인하는"),
    GOOD_LISTENER("GOOD_LISTENER", "👐", "고민을 잘 들어주는"),
    COMPLIMENTER("COMPLIMENTER", "😍", "칭찬을 많이 하는");

    companion object {
        fun fromKeyword(key: String): Trait? {
            return entries.find { it.keyword == key }
        }
    }
}

@Composable
fun KeywordChoice(
    emoji: String,
    text: String,
    selectedOrder: Int?,
    onClick:()->Unit,
    modifier: Modifier =Modifier,
){
    Box(
        modifier=modifier,
    ){
        Surface(
            modifier=Modifier
                .height(33.dp)
                .toggleable(
                value = selectedOrder !=null,
                onValueChange={
                    onClick()
                },
                role= Role.Checkbox,
            ),
            shape= RoundedCornerShape(30.dp),
            color= if (selectedOrder != null){
            MainVariant1
        } else { White},
            border=if (selectedOrder != null){
                null
            }else {
                BorderStroke(
                    width = 1.dp,
                    color = Gray200
                )
            }
        ){
            Row(
                modifier=Modifier.padding(
                    horizontal=15.dp,
                    vertical=5.dp,
                ),
                verticalAlignment=Alignment.CenterVertically,
            ){
                Text(
                    text=emoji,
                    style= CallTheme.typography.bodyMediumMedium
                )
                Spacer(modifier=Modifier.width(7.dp))
                Text(
                    text=text,
                    color=if (selectedOrder !=null){
                        White
                    }else {
                        Black},
                    style= CallTheme.typography.bodyMediumMedium
                )
            }
        }
        if (selectedOrder != null){
            Surface(
                modifier= Modifier
                    .align(Alignment.TopStart)
                    .offset(
                        x=(-5).dp,
                        y=(-5).dp,
                    )
                    .size(20.dp),
                shape= CircleShape,
                color=White,
                border= BorderStroke(
                    width=1.5.dp,
                    color= MainVariant1,
                ),
            ) {
                Box(
                    contentAlignment=Alignment.Center
                ){
                    Text(
                        text=selectedOrder.toString(),
                        color=MainVariant1,
                        style= CallTheme.typography.miniMedium,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "KeywordChoice 선택 전",
    showBackground = true,
    backgroundColor = 0xFFE9E5E7
)
@Composable
private fun KeywordChoiceUnselectedPreview() {
    CallFromAiTheme {
        val trait = Trait.JEALOUS // Trait 객체 직접 지정
        KeywordChoice(
            emoji = trait.emoji,
            text = trait.label,
            selectedOrder = null,
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(
    name = "KeywordChoice 선택 후",
    showBackground = true,
    backgroundColor = 0xFFE9E5E7
)
@Composable
private fun KeywordChoiceSelectedPreview() {
    CallFromAiTheme {
        val trait = Trait.HUMOROUS
        KeywordChoice(
            emoji = trait.emoji,
            text = trait.label,
            selectedOrder = 1,
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
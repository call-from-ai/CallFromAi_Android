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
import kr.co.call.designsystem.theme.White
import kr.co.call.impl.viewmodel.model.Trait

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
                    horizontal=10.dp,
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
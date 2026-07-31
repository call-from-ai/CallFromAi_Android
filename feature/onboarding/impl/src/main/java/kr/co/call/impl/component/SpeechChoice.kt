package kr.co.call.impl.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.MainVariant1
import kr.co.call.designsystem.theme.MainVariant5Chat
import kr.co.call.designsystem.theme.White

@Composable
fun SpeechChoice(
    text: String,
    selected: Boolean,
    onClick: ()->Unit,
    modifier: Modifier =Modifier,
){
    Surface(
        modifier=modifier.selectable(
            selected=selected,
            onClick=onClick,
            role=Role.RadioButton,
        ),
        shape= RoundedCornerShape(10.dp),
        color= if(selected){
            MainVariant5Chat
        }else {White},
        border=if(selected){
            BorderStroke(
                width=1.dp,
                color=MainVariant1,
            )
        }else{null},
    ){
        Text(
            text=text,
            modifier=Modifier.padding(
                horizontal=20.dp,
                vertical=10.dp
            ),
            style= CallTheme.typography.bodyMediumMedium,
            color= MainVariant1,
            textAlign= TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SpeechChoicePreview() {
    CallFromAiTheme {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(17.dp),
        ) {
            SpeechChoice(
                text = "반말",
                selected = false,
                onClick = {},
            )

            SpeechChoice(
                text = "반존대",
                selected = true,
                onClick = {},
            )

            SpeechChoice(
                text = "존댓말",
                selected = false,
                onClick = {},
            )
        }
    }
}
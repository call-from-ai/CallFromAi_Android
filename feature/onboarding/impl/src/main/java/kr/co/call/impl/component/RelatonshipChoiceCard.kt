package kr.co.call.impl.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.ChatGradient
import kr.co.call.designsystem.theme.Gray400
import kr.co.call.designsystem.theme.MainVariant3
import kr.co.call.designsystem.theme.MainVariant5Chat
import kr.co.call.designsystem.theme.White
import kr.co.call.onboarding.impl.R

@Composable
fun RelationshipChoiceCard(
    @DrawableRes imageRes: Int,
    title: String,
    description: String,
    selected: Boolean,
    onClick:() -> Unit,
    modifier: Modifier =Modifier,
){
    Surface(
        modifier = modifier
            .height(172.dp)
            .selectable(
                selected=selected,
                onClick=onClick,
                role=Role.RadioButton,
            ),
        shape= RoundedCornerShape(20.dp),
        color=if (selected){
            MainVariant5Chat
        } else{White},
        border=if(selected){
            BorderStroke(
                width=1.5.dp,
                color= MainVariant3,
            )
        }else {null},
    ){
        Column(
            modifier= Modifier
                .fillMaxSize()
                .padding(
                    horizontal=6.dp,
                    vertical=13.dp,
                ),
            horizontalAlignment=Alignment.CenterHorizontally,
        ){
            Image(
                painter= painterResource(id=imageRes),
                contentDescription=title,
                modifier=Modifier
                    .fillMaxWidth()
                    .height(75.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier=Modifier.height(10.dp))

            Text(
                text=title,
                style= CallTheme.typography.bodyMediumBold.copy(
                    brush=ChatGradient
                ),
                textAlign =TextAlign.Center,
                maxLines=2,
            )

            Spacer(modifier=Modifier.height(5.dp))

            Text(
                text=description,
                style= CallTheme.typography.captionBold,
                color= Gray400,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Preview(
    name = "Relationship Card States",
    showBackground = true,
    widthDp = 280,
)
@Composable
private fun RelationshipChoiceCardPreview() {
    CallFromAiTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RelationshipChoiceCard(
                imageRes = R.drawable.relationship_old,
                title = "오래된 연인",
                description = "편안한 관계",
                selected = false,
                onClick = {},
            )

            RelationshipChoiceCard(
                imageRes = R.drawable.relationship_old,
                title = "오래된 연인",
                description = "편안한 관계",
                selected = true,
                onClick = {},
            )
        }
    }
}
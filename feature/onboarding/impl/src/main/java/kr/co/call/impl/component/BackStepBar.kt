package kr.co.call.impl.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Black
import kr.co.call.onboarding.impl.R

@Composable
fun BackStepBar(
    onBackClick:()->Unit,
    modifier: Modifier =Modifier,
){
    Row(
        modifier=modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(58.dp)
            .padding(
                start=0.dp,
                end=8.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ){
        IconButton(
            onClick=onBackClick,
        ){
            Icon(
                painter= painterResource(R.drawable.ic_back),
                contentDescription="뒤로가기",
                tint= Black,
                modifier=Modifier
                    .size(
                        width=8.dp,
                        height=15.dp
                    )
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 393,
    heightDp=58
)
@Composable
private fun BackTopBarPreview() {
    BackStepBar(
        onBackClick = {}
    )
}

package kr.co.call.impl.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallTheme.typography
import kr.co.call.login.impl.R

@Composable
fun BackTopBar(
    onBackClick:()->Unit,
    modifier: Modifier =Modifier,
){
    Row(
        modifier=modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal=18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
    IconButton(
        onClick=onBackClick,
    ){
        Icon(
            painter= painterResource(R.drawable.ic_arrow_back),
            contentDescription="뒤로가기",
            tint= Black,
            modifier=Modifier
                .size(
                width=8.dp,
                height=17.dp
            )
        )
    }
        Text(
            text="약관",
            color=Black,
            style=typography.bodyLargeBold
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 393,
    heightDp=58
)
@Composable
private fun BackTopBarPreview() {
        BackTopBar(
            onBackClick = {}
        )
    }

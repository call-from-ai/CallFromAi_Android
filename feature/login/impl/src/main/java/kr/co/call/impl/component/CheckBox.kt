package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Gray200
import kr.co.call.designsystem.theme.MainVariant1
import kr.co.call.designsystem.theme.White
import kr.co.call.login.impl.R

@Composable
fun CheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier =Modifier,
){
    Box(
        modifier=modifier
            .size(23.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(
                color=if (checked) MainVariant1 else Gray200,
            )
            .clickable{
                onCheckedChange(!checked)
            },
        contentAlignment= Alignment.Center,
    ){
        if (checked){
            Icon(
                painter=painterResource(R.drawable.ic_check),
                contentDescription="check",
                tint= White,
                modifier=Modifier.size(13.dp)
            )
        }
    }
}

@Preview(
    name = "CheckBox Preview",
    showBackground = true,
)
@Composable
private fun CheckBoxPreview() {
    CheckBox(
        checked = true,
        onCheckedChange = {},
    )
}
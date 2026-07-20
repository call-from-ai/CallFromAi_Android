package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.designsystem.theme.Gray100
import kr.co.call.designsystem.theme.SubRed

@Composable
fun NameBox(
    modifier: Modifier,
    label:String,
    value: String,
    onValueChange: (String) ->Unit,
    ){
    Column(
        modifier=modifier
            .width(125.dp)
    ){
        Row {
            Text(text=label)
            Text(text=" *",
                color= SubRed
            )
        }
        Spacer(modifier=Modifier.height(6.dp))
        Box(
            modifier= Modifier
                .fillMaxWidth()
                .height(51.dp)
                .background(
                    color=Gray100,
                    shape=RoundedCornerShape(10.dp)
                )
                .padding(horizontal=16.dp),
            contentAlignment= Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = CallTheme.typography.bodyMediumMedium,
                        modifier = Modifier
                        .fillMaxWidth()
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun NameBoxPreview() {
    var name by remember {
        mutableStateOf("")
    }

    NameBox(
        modifier = Modifier,
        label = "이름",
        value = name,
        onValueChange = { name = it },
    )
}
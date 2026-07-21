package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.Black
import kr.co.call.designsystem.theme.CallTheme.typography
import kr.co.call.designsystem.theme.Gray400
import kr.co.call.designsystem.theme.Gray900
import kr.co.call.designsystem.theme.MainVariant1

@Composable
fun AgreementItem(
    title: String,
    isRequired: Boolean,
    isChecked: Boolean,
    onCheckedChange: (Boolean)->Unit,
    onViewClick:()->Unit,
    modifier: Modifier =Modifier,
){
    Row(
        modifier=modifier.fillMaxWidth(),
        verticalAlignment= Alignment.CenterVertically,
    ){
        CheckBox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
        )
        Spacer(modifier=Modifier.width(7.dp))
        Text(
            text= if(isRequired)"필수" else "선택",
            color=if(isRequired) MainVariant1 else Gray900,
            style=typography.bodySmallBold,
        )
        Spacer(modifier=Modifier.width(15.dp))
        Text(
            text=title,
            modifier=Modifier.weight(1f),
            style=typography.bodySmall,
            color= Black,
        )
        TextButton(
            onClick=onViewClick,
            contentPadding= PaddingValues(horizontal=3.dp),
            colors= ButtonDefaults.textButtonColors(
                contentColor= Gray400,
            ),
        ){
            Text(
                text="보기",
                style=typography.bodyMedium,
            )
        }
    }
}

@Preview(
    name = "AgreementItem Preview",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun AgreementItemPreview() {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AgreementItem(
            title = "서비스 이용 약관 동의",
            isRequired = true,
            isChecked = true,
            onCheckedChange = {},
            onViewClick = {},
        )

        AgreementItem(
            title = "마케팅 정보 수신 동의",
            isRequired = false,
            isChecked = false,
            onCheckedChange = {},
            onViewClick = {},
        )
    }
}
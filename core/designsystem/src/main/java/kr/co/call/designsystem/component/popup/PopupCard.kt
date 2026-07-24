package kr.co.call.designsystem.component.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallTheme

/**
 * OneButtonPopup / TwoButtonPopup이 공유하는 카드 셸 ( 배경, 그림자, 라벨, 타이틀, 설명 )
 */
@Composable
internal fun PopupCard(
    modifier: Modifier = Modifier,
    label: String,
    title: String?,
    description: AnnotatedString? = null,
    buttons: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 15.dp,
                shape = RoundedCornerShape(30.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f),
            )
            .background(color = CallTheme.colors.background, shape = RoundedCornerShape(30.dp))
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = CallTheme.typography.bodyMediumMedium,
            color = CallTheme.colors.mainVariant1,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (title != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = CallTheme.typography.bodyLargeBold,
                color = CallTheme.colors.black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (description != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = CallTheme.typography.bodySmall,
                color = CallTheme.colors.gray800,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            buttons()
        }
    }
}

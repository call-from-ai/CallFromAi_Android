package kr.co.call.impl.component.chatlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.modifier.skeleton
import kr.co.call.designsystem.theme.CallFromAiTheme

@Composable
fun LoadingColumn(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(102.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .skeleton(isLoading = true)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingColumnPreview() {
    CallFromAiTheme {
        LoadingColumn()
    }
}

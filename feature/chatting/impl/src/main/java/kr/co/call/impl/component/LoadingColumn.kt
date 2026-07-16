package kr.co.call.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.modifier.skeleton
import kr.co.call.designsystem.theme.CallFromAiTheme

@Composable
fun LoadingColumn(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        repeat(5) {
            LoadingItem()
        }
    }
}

@Composable
private fun LoadingItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(102.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 프로필 이미지 플레이스홀더
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .skeleton(isLoading = true)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 이름 + 메시지 플레이스홀더
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray)
                    .skeleton(isLoading = true)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray)
                    .skeleton(isLoading = true)
            )
        }

        // 시간 플레이스홀더
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
                .skeleton(isLoading = true)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingColumnPreview() {
    CallFromAiTheme {
        LoadingColumn()
    }
}

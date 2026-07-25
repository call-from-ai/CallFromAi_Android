package kr.co.call.impl.component.chatroom.ai

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun PhotoBubble(
    photoUrl: String,
    time: String = "",
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 0.dp,
        bottomEnd = 20.dp,
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (time.isNotEmpty()) {
            Text(
                text = time,
                style = CallTheme.typography.caption.copy(
                    color = CallTheme.colors.gray400,
                ),
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        AsyncImage(
            model = photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .clip(shape),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoBubblePreview() {
    CallFromAiTheme {
        PhotoBubble(
            photoUrl = "",
            time = "13:00",
        )
    }
}

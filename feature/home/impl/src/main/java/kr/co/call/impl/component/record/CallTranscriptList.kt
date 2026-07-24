package kr.co.call.impl.component.record

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.home.CallTranscript

/**
 * 통화 내용을 말풍선 목록으로 표시
 */
@Composable
fun CallTranscriptList(
    transcripts: List<CallTranscript>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 20.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 타이틀
        item(key = "recording_content_title") {
            Text(
                text = "녹음 내용",
                color = CallTheme.colors.gray800,
                style = CallTheme.typography.bodySmallBold,
            )
        }

        items(items = transcripts) { transcript ->
            CallTranscriptBubble(transcript = transcript)
        }
    }
}

@Composable
private fun CallTranscriptBubble(
    transcript: CallTranscript,
    modifier: Modifier = Modifier,
) {
    val isUser = transcript.speaker == CallTranscript.Speaker.USER
    val bubbleShape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Text(
            text = transcript.content,
            color = CallTheme.colors.black,
            style = CallTheme.typography.bodyMedium,
            modifier = Modifier
                .widthIn(max = 340.dp)
                .then(
                    if (isUser) {
                        Modifier.background(
                            color = CallTheme.colors.mainVariant5Chat,
                            shape = bubbleShape,
                        )
                    } else {
                        Modifier.border(
                            width = 1.3.dp,
                            color = CallTheme.colors.gray100,
                            shape = bubbleShape,
                        )
                    },
                )
                .padding(horizontal = 15.dp, vertical = 10.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 500)
@Composable
private fun CallTranscriptListPreview() {
    CallFromAiTheme {
        CallTranscriptList(
            transcripts = listOf(
                CallTranscript(
                    content = "여보세요",
                    speaker = CallTranscript.Speaker.USER,
                ),
                CallTranscript(
                    content = "잘 일어났어? 목소리 아직 잠긴 것 같은데.",
                    speaker = CallTranscript.Speaker.AI,
                ),
                CallTranscript(
                    content = "응 방금 일어나 준비하고 있었어",
                    speaker = CallTranscript.Speaker.USER,
                ),
                CallTranscript(
                    content = "오늘 출근이지? 몸은 좀 괜찮아? 피곤해 보여.",
                    speaker = CallTranscript.Speaker.AI,
                ),
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun UserCallTranscriptBubblePreview() {
    CallFromAiTheme {
        CallTranscriptBubble(
            transcript = CallTranscript(
                content = "응 방금 일어나 준비하고 있었어",
                speaker = CallTranscript.Speaker.USER,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun CharacterCallTranscriptBubblePreview() {
    CallFromAiTheme {
        CallTranscriptBubble(
            transcript = CallTranscript(
                content = "잘 일어났어? 목소리 아직 잠긴 것 같은데.",
                speaker = CallTranscript.Speaker.AI,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

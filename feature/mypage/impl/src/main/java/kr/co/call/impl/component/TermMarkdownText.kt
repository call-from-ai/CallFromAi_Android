package kr.co.call.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme

/**
 * 약관 content 렌더러
 * # / ## / ### 등 마크다운 기호만 제거하고, 모든 줄을 동일 Text 스타일로 표시한다.
 */
@Composable
fun TermMarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
) {
    val lines = remember(markdown) { parseTermLines(markdown) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        lines.forEach { line ->
            if (line.isEmpty()) {
                // 빈 줄 간격은 spacedBy 로 처리되므로 skip
            } else {
                Text(
                    text = line,
                    style = CallTheme.typography.bodySmall,
                    color = CallTheme.colors.gray400,
                )
            }
        }
    }
}

/**
 * 마크다운 접두어를 제거한 표시용 줄 목록.
 * 빈 줄은 "" 로 남겨 문단 간격을 유지한다.
 */
internal fun parseTermLines(markdown: String): List<String> {
    if (markdown.isBlank()) return emptyList()

    return markdown
        .replace("\r\n", "\n")
        .lines()
        .map { rawLine ->
            val trimmed = rawLine.trim()
            if (trimmed.isEmpty()) {
                ""
            } else {
                stripMarkdownPrefix(trimmed)
            }
        }
}

private fun stripMarkdownPrefix(line: String): String {
    return when {
        line.startsWith("###") -> line.removePrefix("###").trim()
        line.startsWith("##") -> line.removePrefix("##").trim()
        line.startsWith("#") -> line.removePrefix("#").trim()
        line.startsWith("- ") -> "• ${line.removePrefix("- ").trim()}"
        line.startsWith("* ") -> "• ${line.removePrefix("* ").trim()}"
        else -> line
    }
}

@Preview(showBackground = true)
@Composable
private fun TermMarkdownTextPreview() {
    CallFromAiTheme {
        TermMarkdownText(
            markdown = """
                # 전화왔어 서비스 이용약관

                ## 제1조 목적
                본 약관은 AI 통화 서비스 "전화왔어"의 이용과 관련하여 회사와 회원 간의 권리, 의무 및 책임사항을 정하는 것을 목적으로 합니다.

                ## 제2조 용어의 정의
                1. 서비스란 회원이 설정한 AI 캐릭터와 전화 및 채팅으로 대화할 수 있는 기능을 말합니다.
                2. 회원이란 본 약관에 동의하고 서비스에 가입한 이용자를 말합니다.
            """.trimIndent(),
        )
    }
}

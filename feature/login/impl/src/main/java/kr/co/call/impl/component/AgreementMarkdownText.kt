package kr.co.call.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kr.co.call.designsystem.theme.CallTheme

@Composable
fun AgreementMarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
) {
    val lines = remember(markdown) {
        parseAgreementMarkdownLines(markdown)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        lines.forEach { line ->
            if (line.isNotEmpty()) {
                Text(
                    text = line,
                    style = CallTheme.typography.bodySmall,
                    color = CallTheme.colors.gray400,
                )
            }
        }
    }
}

private fun parseAgreementMarkdownLines(
    markdown: String,
): List<String> {
    if (markdown.isBlank()) return emptyList()

    return markdown
        .replace("\r\n", "\n")
        .lines()
        .map { rawLine ->
            val line = rawLine.trim()

            when {
                line.isEmpty() -> ""
                line.startsWith("### ") ->
                    line.removePrefix("### ").trim()

                line.startsWith("## ") ->
                    line.removePrefix("## ").trim()

                line.startsWith("# ") ->
                    line.removePrefix("# ").trim()

                line.startsWith("- ") ->
                    "• ${line.removePrefix("- ").trim()}"

                line.startsWith("* ") ->
                    "• ${line.removePrefix("* ").trim()}"

                else -> line
            }
        }
}
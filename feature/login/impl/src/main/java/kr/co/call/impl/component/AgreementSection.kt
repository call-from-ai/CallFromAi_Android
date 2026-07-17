package kr.co.call.impl.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.call.designsystem.theme.CallTheme.typography
import kr.co.call.designsystem.theme.Pretendard

private val numberedLineRegex=
    Regex("""^(\d+[.)])\s*(.+)$""")
private val bulletLineRegex=
    Regex("""^[•·-]\s*(.+)$""")
@Composable
fun AgreementSection(
    content:String,
    modifier: Modifier=Modifier,
) {
    Column(modifier=modifier.fillMaxWidth(),
    ) {
        content.lines().forEach { rawLine ->
            if (rawLine.isBlank()){
                Spacer(modifier=Modifier.height(12.dp))
                return@forEach
            }
            val line = rawLine.trim()
            when {
                //글머리표
                bulletLineRegex.matches(line) -> {
                    val matchResult =
                        bulletLineRegex.matchEntire(line)

                    AgreementIndentedLine(
                        marker = "•",
                        text = matchResult
                            ?.groupValues
                            ?.get(1)
                            .orEmpty(),
                        startPadding =10.dp,
                        markerWidth=15.dp,
                    )
                }
                //번호 목록(1. 2. 3.)
                numberedLineRegex.matches(line) -> {
                    val matchResult =
                        numberedLineRegex.matchEntire(line)
                    AgreementIndentedLine(
                        marker = matchResult
                            ?.groupValues
                            ?.get(1)
                            .orEmpty(),
                        text = matchResult
                            ?.groupValues
                            ?.get(2)
                            .orEmpty(),
                        startPadding =0.dp,
                        markerWidth =15.dp,
                    )
                }

                else -> {
                    Text(
                        text = line,
                        style = typography.bodySmall,
                    )
                }
            }
        }
    }
}

    @Composable
    private fun AgreementIndentedLine(
        marker: String,
        text: String,
        startPadding: Dp,
        markerWidth: Dp,
        modifier: Modifier=Modifier,
    ){
        Row(
            modifier=modifier
                .fillMaxWidth()
                .padding(start=startPadding),
            verticalAlignment= Alignment.Top,
        ){
            Text(
                text=marker,
                style=typography.bodySmall,
                modifier=Modifier.width(markerWidth)
            )
            Text(
                text=text,
                style=typography.bodySmall,
                modifier=Modifier.weight(1f),
            )
        }
    }
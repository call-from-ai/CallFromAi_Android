package kr.co.call.impl.component.chatroom.manager

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kr.co.call.designsystem.theme.MainVariant1

/**
 * 주어진 텍스트 내에서 특정 단어의 첫 번째 등장 부분을 강조 표시하는 [AnnotatedString]을 생성합니다.
 *
 * [highlightWord]가 [text] 내에서 발견되면, 해당 단어는 [MainVariant1] 색상으로 스타일이 적용됩니다.
 * 단어를 찾지 못한 경우, 함수는 스타일이 적용되지 않은 원본 텍스트를 반환합니다.
 *
 * @param text 처리할 전체 문자열입니다.
 */
fun highlightText(
    text: String,
    highlightWord: String,
): AnnotatedString {
    return buildAnnotatedString {
        // 강조할 키워드의 시작 위치
        val startIndex = text.indexOf(highlightWord)


        if (startIndex >= 0) {
            // 키워드 앞부분은 기본 스타일로 추가
            append(text.substring(0, startIndex))

            // 키워드에만 강조 색상을 적용
            withStyle(
                SpanStyle(color = MainVariant1)
            ) {
                append(highlightWord)
            }

            // 키워드 이후 문자열을 이어서 추가
            append(
                text.substring(
                    startIndex + highlightWord.length
                )
            )
        } else {
            // 키워드가 없는 경우 원본 문자열을 그대로 표시
            append(text)
        }
    }
}
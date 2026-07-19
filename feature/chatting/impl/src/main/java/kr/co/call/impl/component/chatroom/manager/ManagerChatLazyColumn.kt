package kr.co.call.impl.component.chatroom.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kr.co.call.designsystem.theme.MainVariant1
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.domain.model.chatting.AskToAgentMessage
import kr.co.call.domain.model.chatting.ChangePartnerInfoMessage
import kr.co.call.domain.model.chatting.ChangePartnerInfoMessageType
import kr.co.call.domain.model.chatting.ManagerFirstMessage
import kr.co.call.domain.model.chatting.ManagerFirstMessageType
import kr.co.call.domain.model.chatting.UpdateInfoMessage
import kr.co.call.domain.model.chatting.UpdateInfoMessageType
import kr.co.call.domain.model.chatting.UserMessage
import kr.co.call.domain.model.chatting.WhenCallMessage
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.chatroom.ChatGrayBubble
import kr.co.call.impl.component.chatroom.ChatPinkBubble
import kr.co.call.impl.model.ManagerChatUiItem

@Composable
fun ManagerChatLazyColumn(
    chatItems: List<ManagerChatUiItem>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    bottomPadding: Dp = 0.dp,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(11.dp),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = 13.dp,
            bottom = 13.dp + bottomPadding,   // 오버레이 높이만큼 확보
        ),
    ) {
        items(
            items = chatItems,
            key = { it.message.id }
        ) { item ->
            when(val message = item.message) {

                // 첫 번째 매니저 채팅
                is ManagerFirstMessage -> {
                    when (message.type) {

                        ManagerFirstMessageType.NORMAL -> {
                            // "전화왔어"가 포함된 경우 해당 단어만 브랜드 컬러로 강조
                            val highlightWord = "전화왔어"

                            val annotated = buildAnnotatedString {
                                // 강조할 키워드의 시작 위치
                                val startIndex = message.content.indexOf(highlightWord)

                                if (startIndex >= 0) {
                                    // 키워드 앞부분은 기본 스타일로 추가
                                    append(message.content.substring(0, startIndex))

                                    // 키워드에만 강조 색상을 적용
                                    withStyle(SpanStyle(color = MainVariant1)) {
                                        append(highlightWord)
                                    }

                                    // 키워드 이후 문자열을 이어서 추가
                                    append(message.content.substring(startIndex + highlightWord.length))
                                } else {
                                    // 키워드가 없는 경우 원본 문자열을 그대로 표시
                                    append(message.content)
                                }
                            }
                            ChatGrayBubble(
                                text = annotated,
                                time = item.time,
                                isLoading = item.loadStatus == LoadStatus.Loading
                            )
                        }

                        ManagerFirstMessageType.RELATIONSHIP -> {
                            ChatLightPinkBubbleWithIcon(
                                text = AnnotatedString(message.content),
                                time = item.time,
                                isLoading = item.loadStatus == LoadStatus.Loading
                            )
                        }
                    }
                }

                // 상담원 문의
                is AskToAgentMessage -> {
                    ChatLightPinkBubble(
                        text = AnnotatedString(message.content),
                        time = item.time
                    )
                }

                // 상대방 정보 변경
                is ChangePartnerInfoMessage -> {
                    when (message.type) {
                        ChangePartnerInfoMessageType.NORMAL -> {
                            ChatGrayBubble(
                                text = AnnotatedString(message.content),
                                time = item.time,
                                isLoading = item.loadStatus == LoadStatus.Loading
                            )
                        }
                        ChangePartnerInfoMessageType.NEXT_CONVERSATION -> {
                            ChatLightPinkBubble(
                                text = AnnotatedString(message.content),
                                time = item.time
                            )
                        }
                    }
                }

                // 기록 정보 수정
                is UpdateInfoMessage -> {
                    when (message.type) {
                        UpdateInfoMessageType.NORMAL -> {
                            ChatGrayBubble(
                                text = AnnotatedString(message.content),
                                time = item.time,
                                isLoading = item.loadStatus == LoadStatus.Loading
                            )
                        }
                        UpdateInfoMessageType.APPLIED_NATURALLY -> {
                            ChatLightPinkBubble(
                                text = AnnotatedString(message.content),
                                time = item.time
                            )
                        }
                    }
                }

                is UserMessage -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ChatPinkBubble(
                            text = message.content,
                            time = item.time
                        )
                    }
                }

                // 전화 일정 안내
                is WhenCallMessage -> {
                    ChatGrayBubble(
                        text = AnnotatedString(message.content),
                        time = item.time,
                        isLoading = item.loadStatus == LoadStatus.Loading
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun ManagerChatLazyColumnPreview() {
    val sampleChatItems = listOf(
        ManagerChatUiItem(
            message = ManagerFirstMessage(
                id = "1",
                content = "안녕하세요! 무엇을 도와드릴까요?",
                type = ManagerFirstMessageType.NORMAL,
                createdAt = LocalDateTime.now()
            ),
            time = "오후 2:00"
        ),
        ManagerChatUiItem(
            message = ManagerFirstMessage(
                id = "2",
                content = "대화를 많이 나눌수록,\n더 자연스러운 관계가 생성돼요.",
                type = ManagerFirstMessageType.RELATIONSHIP,
                createdAt = LocalDateTime.now()
            ),
            time = "오후 2:01"
        ),
        ManagerChatUiItem(
            message = ManagerFirstMessage(
                id = "3",
                content = "로딩 중...",
                type = ManagerFirstMessageType.NORMAL,
                createdAt = LocalDateTime.now()
            ),
            loadStatus = LoadStatus.Loading,
            time = "오후 2:02"
        )
    )

    CallFromAiTheme {
        ManagerChatLazyColumn(
            chatItems = sampleChatItems
        )
    }
}

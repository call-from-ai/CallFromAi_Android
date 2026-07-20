package kr.co.call.impl.component.chatroom.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
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

/**
 * 매니저 채팅 화면에서 메시지 타입에 따른 UI 콘텐츠를 렌더링하는 컴포저블 함수입니다.
 *
 * [ManagerChatUiItem]의 메시지 타입을 기반으로 적절한 채팅 버블을 표시하며,
 * 메시지 종류에 따라 스타일 및 강조 처리를 적용합니다.
 *
 * 처리하는 메시지 유형:
 * - [ManagerFirstMessage]: 매니저의 첫 안내 메시지 표시
 *   - 일반 메시지는 특정 키워드를 브랜드 컬러로 강조
 *   - 관계 형성 안내 메시지는 아이콘이 포함된 핑크색 버블 표시
 * - [AskToAgentMessage]: 상담원 문의 요청 메시지를 핑크색 버블로 표시
 * - [ChangePartnerInfoMessage]: 상대방 정보 변경 관련 메시지 표시
 *   - 변경 완료 안내 메시지는 특정 문구를 브랜드 컬러로 강조
 * - [UpdateInfoMessage]: 기록 정보 수정 관련 메시지 표시
 *   - 자연스러운 반영 안내 문구를 브랜드 컬러로 강조
 * - [UserMessage]: 사용자가 보낸 메시지를 우측 정렬된 핑크색 버블로 표시
 * - [WhenCallMessage]: 전화 일정 안내 메시지를 회색 버블로 표시
 *
 * @param item 렌더링할 메시지 데이터와 로딩 상태를 포함하는 UI 모델 객체
 * @param modifier 컴포저블의 레이아웃 및 동작을 제어하기 위한 Modifier
 */
@Composable
fun ManagerChatItemContent(
    item: ManagerChatUiItem.Message,
    modifier: Modifier = Modifier,
) {
    when (val message = item.message) {

        // 첫 번째 매니저 채팅
        is ManagerFirstMessage -> {
            when (message.type) {

                ManagerFirstMessageType.NORMAL -> {
                    ChatGrayBubble(
                        text = highlightText(
                            text = message.content,
                            highlightWord = "전화왔어"
                        ),
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
                        text = highlightText(
                            text = message.content,
                            highlightWord = "다음 대화와 전화부터 바로 반영 돼요."
                        ),
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
                        text = highlightText(
                            text = message.content,
                            highlightWord = "이후 대화에 자연스럽게 반영 돼요!"
                        ),
                        time = item.time
                    )
                }
            }
        }

        // 유저 메세지
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

@Preview(showBackground = true)
@Composable
private fun ManagerChatItemContentPreview() {
    val sampleItems = listOf(
        ManagerChatUiItem.Message(
            message = ManagerFirstMessage(
                id = "1",
                content = "안녕하세요! 전화왔어 매니저입니다.",
                type = ManagerFirstMessageType.NORMAL,
                createdAt = LocalDateTime.now()
            ),
            time = "오후 2:00"
        ),
        ManagerChatUiItem.Message(
            message = ManagerFirstMessage(
                id = "2",
                content = "대화를 많이 나눌수록,\n더 자연스러운 관계가 생성돼요.",
                type = ManagerFirstMessageType.RELATIONSHIP,
                createdAt = LocalDateTime.now()
            ),
            time = "오후 2:01"
        ),
        ManagerChatUiItem.Message(
            message = UserMessage(
                id = "3",
                content = "반가워요!",
                createdAt = LocalDateTime.now()
            ),
            time = "오후 2:02"
        ),
        ManagerChatUiItem.Message(
            message = WhenCallMessage(
                id = "4",
                content = "오늘 오후 3시에 전화 드릴게요.",
                createdAt = LocalDateTime.now()
            ),
            time = "오후 2:03"
        )
    )

    CallFromAiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            sampleItems.forEach { item ->
                ManagerChatItemContent(item = item)
            }
        }
    }
}
package kr.co.call.impl.component.chatroom.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kr.co.call.designsystem.theme.MainVariant1
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
 * 매니저와의 채팅 화면에서 각 메시지 타입에 따른 UI 콘텐츠를 렌더링하는 컴포저블 함수입니다.
 *
 * 이 함수는 [ManagerChatUiItem]의 데이터 모델을 기반으로 하며, 다음과 같은 메시지 유형을 처리합니다:
 * - 매니저의 첫 인사 ([ManagerFirstMessage]): 특정 키워드 강조 및 타입별 버블 스타일 적용
 * - 상담원 문의 ([AskToAgentMessage]): 핑크색 배경의 말풍선 제공
 * - 상대방 정보 변경 및 기록 정보 수정 ([ChangePartnerInfoMessage], [UpdateInfoMessage]): 상태 변화에 따른 회색 또는 핑크색 버블 적용
 * - 유저 본인의 메시지 ([UserMessage]): 우측 정렬된 핑크색 버블 적용
 * - 전화 일정 안내 ([WhenCallMessage]): 회색 배경의 말풍선 제공
 *
 * @param item 렌더링할 채팅 메시지 데이터와 상태 정보를 포함하는 UI 모델 객체
 * @param modifier 컴포저블의 레이아웃 및 동작을 수정하기 위한 [Modifier]
 */
@Composable
fun ManagerChatItemContent(
    item: ManagerChatUiItem,
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
package kr.co.call.impl.component.chatroom.ai

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType
import kr.co.call.impl.component.chatroom.ChatGrayBubble
import kr.co.call.impl.component.chatroom.ChatPinkBubble
import kr.co.call.impl.model.ChatItemUiModel

/**
 * 채팅 화면에서 하나의 메시지 행을 나타내는 Composable 함수입니다.
 *
 * 발신자 유형과 메시지 유형에 따라 말풍선, 이미지 등 서로 다른 UI 형태로 동적으로 렌더링합니다.
 *
 * @param item 메시지 데이터(내용, 발신자, 시간 등)를 포함하는 UI 모델입니다.
 * @param isSelected 메시지가 길게 눌러져 액션 팝업 표시 대상으로 선택되었는지 여부입니다.
 * @param onLongPress 메시지를 길게 눌렀을 때 호출되며, 선택된 메시지의 고유 ID를 전달합니다.
 * @param onCopy 팝업 메뉴에서 "복사" 액션을 선택했을 때 호출됩니다.
 * @param onDelete 팝업 메뉴에서 "삭제" 액션을 선택했을 때 호출됩니다.
 * @param onDismiss 메시지 액션 팝업을 닫을 때 호출됩니다.
 */
@Composable
fun ChatMessageRow(
    item: ChatItemUiModel.Message,
    isSelected: Boolean,
    onLongPress: (Long) -> Unit,
    onCopy: (String) -> Unit,
    onDelete: (Long) -> Unit,
    onDismiss: () -> Unit,
    popupOffsetY: Int,
) {
    when (item.senderType) {
        SenderType.AI -> Box(
            modifier = Modifier.combinedClickable(
                onLongClick = { onLongPress(item.chatMessageId) },
                onClick = {},
            )
        ) {
            ChatGrayBubble(
                text = AnnotatedString(item.content),
                time = item.time,
            )
            if (isSelected) {
                ChatBubblePopUp(
                    offset = IntOffset(0, popupOffsetY),
                    onCopy = { onCopy(item.content) },
                    onDelete = { onDelete(item.chatMessageId) },
                    onDismiss = onDismiss,
                )
            }
        }

        SenderType.USER -> Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Box(
                modifier = Modifier.combinedClickable(
                    onLongClick = { onLongPress(item.chatMessageId) },
                    onClick = {},
                )
            ) {
                when (item.messageType) {
                    MessageType.IMAGE -> PhotoBubble(
                        photoUrl = item.photoUrl,
                        time = item.time,
                    )
                    MessageType.TEXT_IMAGE -> Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        PhotoBubble(photoUrl = item.photoUrl)

                        Spacer(modifier = Modifier.height(11.dp))

                        ChatPinkBubble(
                            text = item.content,
                            time = item.time,
                        )
                    }
                    else -> ChatPinkBubble(
                        text = item.content,
                        time = item.time,
                    )
                }
                if (isSelected) {
                    ChatBubblePopUp(
                        offset = IntOffset(0, popupOffsetY),
                        onCopy = { onCopy(item.content) },
                        onDelete = { onDelete(item.chatMessageId) },
                        onDismiss = onDismiss,
                    )
                }
            }
        }

        SenderType.UNKNOWN -> {}
    }
}
package kr.co.call.impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.chatting.impl.R
import kr.co.call.designsystem.component.popup.TwoButtonPopup
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.chatting.ChatSummary
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.chatlist.ChatListItem
import kr.co.call.impl.component.chatlist.FrontRow
import kr.co.call.impl.component.chatlist.LoadingColumn
import kr.co.call.impl.intent.ChatListIntent
import kr.co.call.impl.sideeffect.ChatListSideEffect
import kr.co.call.impl.state.ChatListState
import kr.co.call.impl.viewmodel.ChatListViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ChatListScreen(
    modifier: Modifier = Modifier,
    onChatRoomClick: (Long) -> Unit,
    onManagerChatRoomClick: () -> Unit,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    // 상태 구독
    val state = viewModel.collectAsState().value

    // 사이드이펙트 수신
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ChatListSideEffect.NavigateToChatRoom -> onChatRoomClick(sideEffect.roomId)
            ChatListSideEffect.NavigateToManagerChatRoom -> onManagerChatRoomClick()
        }
    }

    ChatListScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier
    )
}

@Composable
fun ChatListScreenContent(
    state: ChatListState,
    onIntent: (ChatListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
            .background(CallTheme.colors.mainVariant5Chat)
            .statusBarsPadding()
    ) {
        if (state.showDeleteChatRoomDialog) {
            val description = buildAnnotatedString {
                withStyle(
                    SpanStyle(color = CallTheme.colors.gray800)
                ) {
                    append(stringResource(R.string.delete_chat_room_description))
                }

                append("\n")

                withStyle(
                    SpanStyle(color = CallTheme.colors.mainVariant1)
                ) {
                    append(stringResource(R.string.delete_chat_room_warning))
                }
            }

            // 채팅방 목록에서 지우기 팝업
            TwoButtonPopup(
                label = "목록에서 지우기",
                title = "채팅방에서 나가시겠습니까?",
                description = description,
                positiveText = "확인",
                negativeText = "취소",
                labelSpacerHeight = 13.dp,
                descriptionSpacerHeight = 25.dp,
                onPositiveClick = { onIntent(ChatListIntent.DeleteChatRoom(state.deleteTargetRoomId)) },
                onNegativeClick = { onIntent(ChatListIntent.DismissDeleteDialog) },
                onDismissRequest = { onIntent(ChatListIntent.DismissDeleteDialog) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "채팅",
            style = CallTheme.typography.titleMediumBold,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            when (state.status) {
                is LoadStatus.Error -> {
                    //TODO: 에러 플레이스홀더 처리
                }
                LoadStatus.Loading -> {
                    item {
                        LoadingColumn()
                    }
                }
                LoadStatus.Idle -> {
                    items(
                        count = state.chatList.size,
                        key = { index -> state.chatList[index].chatRoomId }
                    ) { index ->
                        val chatSummary = state.chatList[index]

                        ChatListItem(
                            chatSummary = chatSummary,
                            onItemClick = {
                                onIntent(
                                    ChatListIntent.ClickChatRoom(
                                        roomId = chatSummary.chatRoomId
                                    )
                                )
                            },
                            onAlarmClick = {
                                onIntent(
                                    ChatListIntent.UpdateAlarmSetting(
                                        roomId = chatSummary.chatRoomId
                                    )
                                )
                            },
                            onDeleteClick = {
                                onIntent(
                                    ChatListIntent.ClickDeleteChatRoom(
                                        roomId = chatSummary.chatRoomId
                                    )
                                )
                            }
                        )
                    }

                    // 매니저는 맨 아래 고정
                    item {
                        FrontRow(
                            isManager = true,
                            chatSummary = ChatSummary(
                                name = "전화왔어 매니저",
                                content = "수현님, 반가워요! 👋🏻 오늘은 어떤 이야기를",
                            ),
                            onClick = { onIntent(ChatListIntent.ClickManagerChatRoom) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatListScreenContentPreview() {
    CallFromAiTheme {
        ChatListScreenContent(
            state = ChatListState(
                chatList = listOf(
                    ChatSummary(
                        chatRoomId = 1,
                        name = "김민지",
                        isMainCharacter = true,
                        content = "오늘 저녁에 뭐해?",
                        whenSubmitted = "30분 전",
                        unReadMessageCount = "3",
                        isAlarmEnabled = true,
                    ),
                    ChatSummary(
                        chatRoomId = 2,
                        name = "이철수",
                        isMainCharacter = false,
                        content = "내일 봐!",
                        whenSubmitted = "1시간 전",
                        unReadMessageCount = "0",
                        isAlarmEnabled = false,
                    )
                ),
                status = LoadStatus.Idle,
                showDeleteChatRoomDialog = true
            ),
            onIntent = {}
        )
    }
}


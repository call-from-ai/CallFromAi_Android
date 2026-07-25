package kr.co.call.impl.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import kr.co.call.chatting.impl.R
import kr.co.call.designsystem.component.popup.TwoButtonPopup
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.impl.component.chatroom.ai.ChatLazyColumn
import kr.co.call.impl.component.chatroom.ai.ChatTextField
import kr.co.call.impl.model.ChatItemUiModel
import kr.co.call.impl.model.TopHeader
import kr.co.call.domain.model.chatting.MessageType
import kr.co.call.domain.model.chatting.SenderType
import kr.co.call.impl.component.chatroom.ai.ChatTopBar
import kr.co.call.impl.component.chatroom.ai.ProfileImageOverlay
import kr.co.call.impl.intent.ChatRoomIntent
import kr.co.call.impl.sideeffect.ChatRoomSideEffect
import kr.co.call.impl.state.ChatRoomUiState
import kr.co.call.impl.util.createImageUri
import kr.co.call.impl.util.toImageData
import kr.co.call.impl.viewmodel.ChatRoomViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ChatRoomScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatRoomViewModel,
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current

    val state = viewModel.collectAsState().value
    val pagingItems: LazyPagingItems<ChatItemUiModel> = viewModel.chats.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    var cameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let {
                viewModel.handleIntent(
                    ChatRoomIntent.PictureTaken(it)
                )
            }
        }
    }

    // Photo Picker 실행 후 사진 선택 결과를 받아 Intent 전달
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.handleIntent(
                ChatRoomIntent.ImagesPicked(it)
            )
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ChatRoomSideEffect.ShowToast -> {}
            is ChatRoomSideEffect.Call -> {}

            ChatRoomSideEffect.GoToCamera -> {
                val uri = context.createImageUri()
                cameraUri = uri
                takePicture.launch(uri)
            }

            ChatRoomSideEffect.GoToGallery -> {
                pickMedia.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }

    ChatRoomScreenContent(
        modifier = modifier,
        state = state,
        pagingItems = pagingItems,
        listState = listState,
        onBack = onBack,
        onIntent = viewModel::handleIntent,
        onValueChange = viewModel::onTextChange,
    )
}

@Composable
fun ChatRoomScreenContent(
    modifier: Modifier = Modifier,
    state: ChatRoomUiState = ChatRoomUiState(),
    pagingItems: LazyPagingItems<ChatItemUiModel>,
    listState: LazyListState = rememberLazyListState(),
    onBack: () -> Unit = {},
    onIntent: (ChatRoomIntent) -> Unit = {},
    onValueChange: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val density = LocalDensity.current
    var overlayHeight by remember { mutableStateOf(0.dp) }

    val imeBottom = WindowInsets.ime.getBottom(density)

    // 키보드 올라올 때 최신 메시지로 자연스럽게 따라 스크롤
    LaunchedEffect(imeBottom) {
        if (imeBottom > 0) {
            listState.scrollToItem(0)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CallTheme.colors.background)
        ) {
            // 탑바: 고정
            ChatTopBar(
                modifier = Modifier.fillMaxWidth(),
                item = state.topHeader,
                onBack = onBack,
                onCallClick = { onIntent(ChatRoomIntent.ClickCall(state.topHeader.characterId)) },
                onProfileClick = { onIntent(ChatRoomIntent.ClickProfile(state.topHeader.imgUrl)) },
            )

            if (state.showDeleteChatRoomDialog) {
                TwoButtonPopup(
                    label = stringResource(id = R.string.chat_room_call_label),
                    title = stringResource(
                        id = R.string.chat_room_call_title,
                        state.topHeader.name
                    ),
                    positiveText = "연결",
                    negativeText = "취소",
                    onPositiveClick = { onIntent(ChatRoomIntent.ClickCall(state.topHeader.characterId)) },
                    onNegativeClick = { onIntent(ChatRoomIntent.DismissDeleteDialog) },
                    onDismissRequest = { onIntent(ChatRoomIntent.DismissDeleteDialog) },
                )
            }

            // 리스트 + 텍스트필드 겹침 영역. 이 영역만 키보드 따라 올라감
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .imePadding()
            ) {
                // 리스트: Box 전체를 채움 (텍스트필드 뒤까지 깔림)
                ChatLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    pagingItems = pagingItems,
                    realtimeMessages = state.chatItems,
                    listState = listState,
                    bottomPadding = overlayHeight,
                    deletedIds = state.deletedIds,
                    selectedMessageId = state.selectedMessageId,
                    onLongPress = { onIntent(ChatRoomIntent.LongPressMessage(it)) },
                    onCopy = { clipboardManager.setText(AnnotatedString(it)) },
                    onDelete = { onIntent(ChatRoomIntent.DeleteMessage(it)) },
                    onDismiss = { onIntent(ChatRoomIntent.DismissPopup) },
                )

                // 텍스트필드: 리스트 위에 겹쳐서 바닥에 고정
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .onSizeChanged {
                            overlayHeight = with(density) { it.height.toDp() }
                        }
                ) {
                    ChatTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        state = state.textFieldState,
                        onValueChange = onValueChange,
                        onCameraClick = { onIntent(ChatRoomIntent.ClickCamera) },
                        onGalleryClick = { onIntent(ChatRoomIntent.ClickGallery) },
                        onSendClick = {
                            onIntent(
                                ChatRoomIntent.SendMessage(
                                    message = state.textFieldState.text,
                                    image = state.textFieldState.selectedImage?.let(context::toImageData),
                                )
                            )
                        },
                        onRemoveImage = { onIntent(ChatRoomIntent.CancelImage) },
                    )

                    Spacer(Modifier.height(15.dp))
                }
            }
        }

        // 프사 확대 오버레이 (가장 위 레이어)
        state.expandedProfileUrl?.let { url ->
            ProfileImageOverlay(
                imageUrl = url,
                onDismiss = { onIntent(ChatRoomIntent.DismissProfile) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatRoomScreenContentPreview() {
    val dummyItems = listOf(
        ChatItemUiModel.DateSeparator(date = "2026년 7월 1일"),
        ChatItemUiModel.Message(
            chatMessageId = 1L,
            senderType = SenderType.AI,
            content = "오늘 하루 어땠어?",
            messageType = MessageType.TEXT,
            time = "13:00",
        ),
        ChatItemUiModel.Message(
            chatMessageId = 2L,
            senderType = SenderType.USER,
            content = "좋았어!",
            messageType = MessageType.TEXT,
            time = "13:01",
        ),
        ChatItemUiModel.Message(
            chatMessageId = 3L,
            senderType = SenderType.AI,
            content = "그랬구나, 오늘도 수고했어.",
            messageType = MessageType.TEXT,
            time = "13:02",
        ),
    )
    val pagingItems = flowOf(PagingData.from(dummyItems)).collectAsLazyPagingItems()

    CallFromAiTheme {
        ChatRoomScreenContent(
            state = ChatRoomUiState(
                topHeader = TopHeader(
                    name = "AI 친구",
                    dDay = "D+ 12"
                )
            ),
            pagingItems = pagingItems,
        )
    }
}

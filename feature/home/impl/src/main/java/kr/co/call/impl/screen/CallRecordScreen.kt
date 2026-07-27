package kr.co.call.impl.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.lifecycle.compose.LifecycleStartEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.home.CallTranscript
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.record.CallRecordLoadingContent
import kr.co.call.impl.component.record.CallRecordPlayer
import kr.co.call.impl.component.record.CallRecordPlayerState
import kr.co.call.impl.component.record.CallRecordSummary
import kr.co.call.impl.component.record.CallRecordTopBar
import kr.co.call.impl.component.record.CallTranscriptList
import kr.co.call.impl.viewmodel.CallRecordIntent
import kr.co.call.impl.viewmodel.CallRecordSideEffect
import kr.co.call.impl.viewmodel.CallRecordViewModel
import kr.co.call.impl.viewmodel.model.CallRecordUiModel
import kr.co.call.impl.viewmodel.state.CallRecordState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * 통화 정보와 대화 내용, 고정 재생 바 표시하는 화면
 */
@Composable
fun CallRecordScreen(
    modifier: Modifier = Modifier,
    callId: Long,
    onBackClick: () -> Unit,
    viewModel: CallRecordViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val recordingUrl = state.record.recordingUrl

    // 같은 녹음 파일은 화면이 재생성되어도 마지막 재생 위치를 복원
    var savedPositionMillis by rememberSaveable(recordingUrl) {
        mutableLongStateOf(0L)
    }

    val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
        .build()

    // ExoPlayer
    val player = remember {
        ExoPlayer.Builder(context.applicationContext)
            .setAudioAttributes(
                audioAttributes,
                true, // 오디오 포커스 자동 관리
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                volume = 1f
            }
    }

    // 앱이 백그라운드로 이동하면 현재 위치를 유지한 채 재생만 일시정지
    LifecycleStartEffect(player) {
        onStopOrDispose {
            player.pause()
        }
    }

    // 플레이어 상태
    var playerState by remember(recordingUrl) {
        mutableStateOf(
            CallRecordPlayerState(
                currentPositionMillis = savedPositionMillis,
            ),
        )
    }

    // 녹음 파일에서 선택한 위치로 이동하는 함수
    fun seekTo(positionMillis: Long) {
        // 최대 위치
        val maximumPosition = playerState.durationMillis
            .takeIf { it > 0L}
            ?: Long.MAX_VALUE

        // 재생 범위 제한
        val targetPosition = positionMillis.coerceIn(
            minimumValue = 0L,
            maximumValue = maximumPosition,
        )

        // 현재 재생중인 위치로 이동
        player.seekTo(targetPosition)

        // 상태값에 현재 위치 저장
        playerState = playerState.copy(
            currentPositionMillis = targetPosition,
        )
        savedPositionMillis = targetPosition
    }

    LaunchedEffect(callId) {
        viewModel.handleIntent(
            CallRecordIntent.Load(callId),
        )
    }

    // play와 url을 가져와서 재생 준비 처리
    LaunchedEffect(
        player,
        recordingUrl,
    ) {
        playerState = CallRecordPlayerState(
            currentPositionMillis = savedPositionMillis,
        )

        if (recordingUrl.isNullOrBlank()) {
            player.clearMediaItems()
            return@LaunchedEffect
        }
        player.setMediaItem(
            MediaItem.fromUri(recordingUrl),
            savedPositionMillis,
        )
        // 재생 준비
        player.prepare()
    }

    LaunchedEffect(
        player,
        playerState.isPlaying,
    ) {
        while (
            isActive && playerState.isPlaying
        ) {
            val currentPositionMillis = player.currentPosition.coerceAtLeast(0L)
            playerState = playerState.copy(
                currentPositionMillis = currentPositionMillis,
            )
            savedPositionMillis = currentPositionMillis
            delay(250L)
        }
    }


    DisposableEffect(player) {
        val listener = object : Player.Listener {
            // 재생 상태가 바뀌면 재생 여부를 전달
            override fun onIsPlayingChanged(isPlaying: Boolean){
                playerState = playerState.copy(
                    isPlaying = isPlaying,
                )
            }
            // 플레이어가 준비 상태일 때 전체 음성 길이 가져옴
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        val durationMillis =
                            if (
                                player.duration != C.TIME_UNSET &&
                                player.duration > 0L
                            ) {
                                player.duration
                            } else {
                                0L
                            }

                        playerState = playerState.copy(
                            durationMillis = durationMillis,
                        )
                    }

                    Player.STATE_ENDED -> {
                        savedPositionMillis = playerState.durationMillis
                        playerState = playerState.copy(
                            currentPositionMillis =
                                playerState.durationMillis,
                            isPlaying = false,
                        )
                    }
                }

            }

            override fun onPlayerError(error: PlaybackException) {
                playerState = playerState.copy(
                    isPlaying = false,
                )
                Toast.makeText(
                    context,
                    "녹음 파일을 재생하지 못했습니다.",
                    Toast.LENGTH_SHORT,
                ).show()
            }

        }
        // listener를 ExoPlayer에 등록
        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is CallRecordSideEffect.ShowMessage -> {
                Toast.makeText(
                    context,
                    sideEffect.message,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    CallRecordScreenContent(
        state = state,
        playerState = playerState,
        onBackClick = onBackClick,
        onSeek = ::seekTo,
        // 5초 전으로 이동
        onRewindClick = {
            seekTo(
                player.currentPosition - 5_000L,
            )
        },
        // 재생,정지버튼
        onPlayPauseClick = {
            if (player.isPlaying) {
                player.pause()
            } else {
                // 재생 종료되었다면 0으로 초기화
                if (player.playbackState == Player.STATE_ENDED) {
                    seekTo(0L)
                }
                player.play()
            }
        },
        // 5초 후로 이동
        onForwardClick = {
            seekTo(
                player.currentPosition + 5_000L,
            )
        },
        modifier = modifier,
    )
}



@Composable
fun CallRecordScreenContent(
    state: CallRecordState,
    playerState: CallRecordPlayerState,
    onBackClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onRewindClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onForwardClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.white),
    ) {
        CallRecordTopBar(
            onBackClick = onBackClick,
        )

        when (state.loadStatus) {
            LoadStatus.Loading -> {
                CallRecordLoadingContent(
                    modifier = Modifier.weight(1f),
                )
            }

            is LoadStatus.Error -> {
                Spacer(
                    modifier = Modifier.weight(1f),
                )
            }

            LoadStatus.Idle -> {
                CallRecordSummary(
                    record = state.record,
                )

                CallTranscriptList(
                    transcripts = state.transcripts,
                    modifier = Modifier.weight(1f),
                )

                CallRecordPlayer(
                    state = playerState,
                    onSeek = onSeek,
                    onRewindClick = onRewindClick,
                    onPlayPauseClick = onPlayPauseClick,
                    onForwardClick = onForwardClick,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun CallRecordScreenPreview() {
    CallFromAiTheme {
        CallRecordScreenContent(
            state = CallRecordState(
                record = CallRecordUiModel(
                    title = "출근 준비와 아침 일정 이야기",
                    calledAtText = "6월 27일 오전 7시 31분",
                    characterName = "민준",
                ),
                transcripts = listOf(
                    "여보세요",
                    "잘 일어났어? 목소리 아직 잠긴 것 같은데.",
                    "응 방금 일어나 준비하고 있었어",
                    "오늘 출근이지? 몸은 좀 괜찮아? 피곤해 보여.",
                    "어제 조금 늦게 자서 그런가 봐. 그래도 괜찮아",
                    "다행이다. 너무 무리하지 말고 천천히 준비해.",
                    "응 이제 씻고 나가려고",
                    "오늘 오전 일정은 어떻게 돼?",
                    "11시에 회의 하나 있고, 그거 끝나면 바로 외부 미팅이 있어.",
                    "점심은?",
                    "팀원들이랑 먹기로 했어.",
                    "뭐 먹을지 정했어?",
                    "아직. 근처 가서 먹으려고.",
                ).mapIndexed { index, content ->
                    CallTranscript(
                        content = content,
                        speaker = if (index % 2 == 0) {
                            CallTranscript.Speaker.USER
                        } else {
                            CallTranscript.Speaker.AI
                        },
                    )
                },
                loadStatus = LoadStatus.Idle,
            ),
            playerState = CallRecordPlayerState(
                currentPositionMillis = 31_000L,
                durationMillis = 81_000L,
            ),
            onBackClick = {},
            onSeek = {},
            onRewindClick = {},
            onPlayPauseClick = {},
            onForwardClick = {},
        )
    }
}

package kr.co.call.impl.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.model.call.CallSessionState
import kr.co.call.impl.component.CallControlPanel
import kr.co.call.impl.component.CallDuration
import kr.co.call.impl.component.CallGradientBackground
import kr.co.call.impl.component.CallHeader
import kr.co.call.impl.component.CallProfile
import kr.co.call.impl.component.CallProfileStyle
import kr.co.call.impl.component.ScreenBlocker
import kr.co.call.impl.viewmodel.CallIntent
import kr.co.call.impl.viewmodel.CallSideEffect
import kr.co.call.impl.viewmodel.CallViewModel
import kr.co.call.impl.viewmodel.model.CallCharacterUiModel
import kr.co.call.impl.viewmodel.state.CallPhase
import kr.co.call.impl.viewmodel.state.CallState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * 통화 중 화면
 */
@Composable
fun CallScreen(
    characterId: Long,
    isIncoming: Boolean,
    onCallFinished: () -> Unit,
    callId: Long = 0L,
    characterName: String = "",
    characterImageUrl: String? = null,
    modifier: Modifier = Modifier,
    viewModel: CallViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val sessionState by viewModel.sessionState.collectAsState()
    val character by viewModel.character.collectAsState()
    val context = LocalContext.current
    val microphonePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.handleIntent(
                CallIntent.MicrophonePermissionResult(isGranted),
            )
        },
    )

    LaunchedEffect(callId, characterId, isIncoming) {
        viewModel.handleIntent(
            CallIntent.Initialize(
                callId = callId,
                characterId = characterId,
                isIncoming = isIncoming,
                characterName = characterName,
                characterImageUrl = characterImageUrl,
            ),
        )

        val isMicrophonePermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO,
        ) == PackageManager.PERMISSION_GRANTED

        if (isMicrophonePermissionGranted) {
            viewModel.handleIntent(
                CallIntent.MicrophonePermissionResult(isGranted = true),
            )
        } else {
            microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            CallSideEffect.FinishCall -> onCallFinished()
            is CallSideEffect.ShowMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 통화 화면에서는 뒤로가기로 그냥 나가지지 않게 막고, 연결 중·통화 중이면 끊기와 동일하게 처리
    BackHandler(enabled = state.phase != CallPhase.ENDED) {
        if (state.phase == CallPhase.CONNECTING || state.phase == CallPhase.READY) {
            viewModel.handleIntent(CallIntent.EndCall)
        }
    }

    CallContent(
        state = state,
        character = character,
        sessionState = sessionState,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

@Composable
private fun CallContent(
    state: CallState,
    character: CallCharacterUiModel,
    sessionState: CallSessionState,
    onIntent: (CallIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state.phase) {
        CallPhase.CONNECTING -> {
            CallSessionContent(
                state = state,
                character = character,
                sessionState = sessionState,
                statusContent = {
                    Text(
                        text = "휴대전화 연결 중...",
                        color = CallTheme.colors.gray900,
                        style = CallTheme.typography.bodyMedium,
                    )
                },
                onIntent = onIntent,
                modifier = modifier,
            )
        }

        CallPhase.READY -> {
            CallSessionContent(
                state = state,
                character = character,
                sessionState = sessionState,
                statusContent = {
                    CallDuration(durationSeconds = state.durationSeconds)
                },
                onIntent = onIntent,
                modifier = modifier,
            )
        }

        CallPhase.ENDING -> {
            CallSessionContent(
                state = state,
                character = character,
                sessionState = sessionState,
                statusContent = {
                    Text(
                        text = if (state.isPreparingSummary) {
                            "통화를 요약하는 중..."
                        } else {
                            "통화를 저장하는 중..."
                        },
                        color = CallTheme.colors.gray900,
                        style = CallTheme.typography.bodyMedium,
                    )
                },
                onIntent = onIntent,
                controlsEnabled = false,
                modifier = modifier,
            )
        }

        CallPhase.ENDED,
        CallPhase.ERROR -> {
            CallEndedScreen(
                state = state,
                character = character,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun CallSessionContent(
    state: CallState,
    character: CallCharacterUiModel,
    sessionState: CallSessionState,
    statusContent: @Composable () -> Unit,
    onIntent: (CallIntent) -> Unit,
    modifier: Modifier = Modifier,
    controlsEnabled: Boolean = true,
) {
    CallGradientBackground(modifier = modifier) {
        ScreenBlocker(modifier = Modifier.fillMaxSize())

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isCompact = maxHeight < 700.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(if (isCompact) 12.dp else 22.dp))
                CallHeader(isIncoming = state.isIncoming)
                Spacer(modifier = Modifier.height(13.dp))
                statusContent()
                Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 16.dp))
                Text(
                    text = character.name,
                    color = CallTheme.colors.gray900,
                    style = CallTheme.typography.titleSuperBig,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 38.dp))
                CallProfile(
                    profileImageUrl = character.profileImageUrl,
                    style = CallProfileStyle.session(isCompact),
                )
                Spacer(modifier = Modifier.weight(1f))
                CallControlPanel(
                    isMicrophoneEnabled = sessionState.isMicrophoneEnabled,
                    isSpeakerEnabled = sessionState.isSpeakerEnabled,
                    onMicrophoneClick = {
                        onIntent(CallIntent.ToggleMicrophone)
                    },
                    onEndCallClick = {
                        onIntent(CallIntent.EndCall)
                    },
                    onSpeakerClick = {
                        onIntent(CallIntent.ToggleSpeaker)
                    },
                    enabled = controlsEnabled,
                )
                Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun CallSendingContentPreview() {
    CallFromAiTheme {
        CallContent(
            state = CallState(
                isIncoming = false,
                phase = CallPhase.CONNECTING,
            ),
            character = CallCharacterUiModel(name = "민준"),
            sessionState = CallSessionState(),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun InCallContentPreview() {
    CallFromAiTheme {
        CallContent(
            state = CallState(
                isIncoming = true,
                phase = CallPhase.READY,
                durationSeconds = 38,
            ),
            character = CallCharacterUiModel(name = "민준"),
            sessionState = CallSessionState(),
            onIntent = {},
        )
    }
}

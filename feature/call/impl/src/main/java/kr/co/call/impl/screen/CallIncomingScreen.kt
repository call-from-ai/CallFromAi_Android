package kr.co.call.impl.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.designsystem.theme.CallTheme
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.CallHeader
import kr.co.call.impl.component.CallProfile
import kr.co.call.impl.component.CallProfileStyle
import kr.co.call.impl.component.IncomingCallActions
import kr.co.call.impl.viewmodel.CallIncomingIntent
import kr.co.call.impl.viewmodel.CallIncomingSideEffect
import kr.co.call.impl.viewmodel.CallIncomingViewModel
import kr.co.call.impl.viewmodel.model.CallCharacterUiModel
import kr.co.call.impl.viewmodel.model.CallDirection
import kr.co.call.impl.viewmodel.state.CallIncomingState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * 전화가 걸려올 때 표시되는 화면
 * - 이동 처리는 웹소켓 연결 후 구현 진행합니다.
 */
@Composable
fun CallIncomingScreen(
    callId: Long,
    characterId: Long,
    onNavigateToCall: (Long, Long) -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    onShowMessage: (String) -> Unit = {},
    viewModel: CallIncomingViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val microphonePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.handleIntent(
                CallIncomingIntent.MicrophonePermissionResult(isGranted),
            )
        },
    )

    LaunchedEffect(callId, characterId) {
        viewModel.handleIntent(
            CallIncomingIntent.Initialize(
                callId = callId,
                characterId = characterId,
            ),
        )
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is CallIncomingSideEffect.RequestMicrophonePermission -> {
                microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            is CallIncomingSideEffect.NavigateToCall -> {
                onNavigateToCall(
                    sideEffect.callId,
                    sideEffect.characterId,
                )
            }

            CallIncomingSideEffect.Finish -> onFinished()

            is CallIncomingSideEffect.ShowMessage -> {
                onShowMessage(sideEffect.message)
            }
        }
    }

    CallIncomingContent(
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

@Composable
private fun CallIncomingContent(
    state: CallIncomingState,
    onIntent: (CallIncomingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CallTheme.colors.mainVariant5Chat),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isCompact = maxHeight < 760.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(if (isCompact) 20.dp else 55.dp))
                CallHeader(direction = CallDirection.INCOMING)
                Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 18.dp))
                Text(
                    text = state.character.name,
                    color = CallTheme.colors.gray900,
                    style = CallTheme.typography.titleSuperBig,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(if (isCompact) 20.dp else 43.dp))
                CallProfile(
                    profileImageUrl = state.character.profileImageUrl,
                    style = CallProfileStyle.incoming(isCompact),
                )
                Spacer(modifier = Modifier.weight(1f))
                IncomingCallActions(
                    onAccept = {
                        onIntent(CallIncomingIntent.AcceptCall)
                    },
                    onReject = {
                        onIntent(CallIncomingIntent.RejectCall)
                    },
                    enabled = state.loadStatus != LoadStatus.Loading,
                )
                Spacer(modifier = Modifier.height(if (isCompact) 24.dp else 48.dp))
            }
        }

        if (state.loadStatus == LoadStatus.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = CallTheme.colors.mainVariant1,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun CallIncomingScreenPreview() {
    CallFromAiTheme {
        CallIncomingContent(
            state = CallIncomingState(
                character = CallCharacterUiModel(
                    name = "민준",
                    profileImageUrl = null,
                ),
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
private fun CompactCallIncomingScreenPreview() {
    CallFromAiTheme {
        CallIncomingContent(
            state = CallIncomingState(
                character = CallCharacterUiModel(
                    name = "민준",
                    profileImageUrl = null,
                ),
            ),
            onIntent = {},
        )
    }
}

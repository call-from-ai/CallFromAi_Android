package kr.co.call.impl.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.call.domain.model.call.IncomingCall
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.component.IncomingCallDialog
import kr.co.call.impl.viewmodel.CallIncomingIntent
import kr.co.call.impl.viewmodel.CallIncomingSideEffect
import kr.co.call.impl.viewmodel.CallIncomingViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * 앱 내부 착신 모달과 통화 수락·거절 상태를 연결합니다.
 */
@Composable
fun IncomingCallDialogRoute(
    call: IncomingCall,
    onNavigateToChatRoom: (Long?) -> Unit,
    onAccepted: (callId: Long, characterId: Long) -> Unit,
    onRejected: (callId: Long) -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: CallIncomingViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    // 통화 수락 전 마이크 권한 요청 결과를 ViewModel로 전달
    val microphonePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.handleIntent(
                CallIncomingIntent.MicrophonePermissionResult(
                    isGranted = isGranted,
                ),
            )
        },
    )

    // 새로운 착신 정보가 들어올 때 ViewModel 상태 초기화
    LaunchedEffect(call.callId, call.characterId) {
        viewModel.handleIntent(
            CallIncomingIntent.Initialize(
                callId = call.callId,
                characterId = call.characterId,
                characterName = call.characterName,
                characterImageUrl = call.characterImageUrl,
            ),
        )
    }

    // API 처리 결과에 따라 권한 요청·화면 이동·모달 종료 수행
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is CallIncomingSideEffect.RequestMicrophonePermission -> {
                microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            is CallIncomingSideEffect.NavigateToCall -> {
                onAccepted(
                    sideEffect.callId,
                    sideEffect.characterId,
                )
            }

            CallIncomingSideEffect.Finish -> {
                onRejected(call.callId)
            }

            is CallIncomingSideEffect.ShowMessage -> {
                onShowMessage(sideEffect.message)
            }
        }
    }

    IncomingCallDialog(
        characterName = call.characterName,
        profileImageUrl = call.characterImageUrl,
        onNavigateToChatRoom = {
            onNavigateToChatRoom(call.chatRoomId)
        },
        onAccept = {
            viewModel.handleIntent(CallIncomingIntent.AcceptCall)
        },
        onReject = {
            viewModel.handleIntent(CallIncomingIntent.RejectCall)
        },
        // API 요청 중 중복 수락·거절·화면 이동 방지
        isActionEnabled = state.loadStatus != LoadStatus.Loading,
    )
}

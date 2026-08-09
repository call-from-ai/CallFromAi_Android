package kr.co.call.callfromai.incomingcall

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kr.co.call.callfromai.incomingcall.notification.CallNotificationManager
import kr.co.call.designsystem.theme.CallFromAiTheme
import kr.co.call.domain.model.call.IncomingCall
import kr.co.call.impl.screen.CallIncomingScreen
import kr.co.call.impl.screen.CallScreen
import kr.co.call.impl.viewmodel.CallViewModel
import javax.inject.Inject

/**
 * 잠금 화면 또는 앱 외부에서 수신 전화를 표시하는 전체 화면 Activity입니다.
 *
 * [createIntent]로 전달받은 통화 식별자를 사용해 착신 화면을 표시하고,
 * 수락이 완료되면 같은 Activity 안에서 실제 통화 화면으로 전환합니다.
 * 거절은 [CallNotificationActionReceiver]에서 관리합니다.
 */
@AndroidEntryPoint
class IncomingCallActivity : ComponentActivity() {

    @Inject
    lateinit var callNotificationManager: CallNotificationManager

    private var incomingCallArgs by mutableStateOf<IncomingCallArgs?>(null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            )
        }

        enableEdgeToEdge()

        incomingCallArgs = intent.toIncomingCallArgs()
        if (incomingCallArgs == null) {
            finish()
            return
        }

        setContent {
            CallFromAiTheme {
                val args = incomingCallArgs ?: return@CallFromAiTheme
                var activeCallId by rememberSaveable(args.callId) {
                    mutableLongStateOf(INVALID_ID)
                }
                var activeCharacterId by rememberSaveable(args.callId) {
                    mutableLongStateOf(INVALID_ID)
                }

                if (activeCallId == INVALID_ID || activeCharacterId == INVALID_ID) {
                    CallIncomingScreen(
                        callId = args.callId,
                        characterId = args.characterId,
                        characterName = args.characterName,
                        characterImageUrl = args.characterImageUrl,
                        autoAcceptOnLaunch = args.autoAccept,
                        onNavigateToCall = { callId, characterId, _, _ ->
                            callNotificationManager.cancel(callId)
                            activeCallId = callId
                            activeCharacterId = characterId
                        },
                        onFinished = {
                            callNotificationManager.cancel(args.callId)
                            finishAndRemoveTask()
                        },
                        onShowMessage = ::showMessage,
                    )
                } else {
                    val callViewModel = hiltViewModel<CallViewModel>(
                        key = "incoming-call-$activeCallId",
                    )

                    CallScreen(
                        callId = activeCallId,
                        characterId = activeCharacterId,
                        characterName = args.characterName,
                        characterImageUrl = args.characterImageUrl,
                        isIncoming = true,
                        onCallFinished = {
                            finishAndRemoveTask()
                        },
                        viewModel = callViewModel,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val newArgs = intent.toIncomingCallArgs()
        if (newArgs == null) {
            finish()
            return
        }
        incomingCallArgs = newArgs
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun Intent.toIncomingCallArgs(): IncomingCallArgs? {
        val callId = getLongExtra(EXTRA_CALL_ID, INVALID_ID)
        val characterId = getLongExtra(EXTRA_CHARACTER_ID, INVALID_ID)

        if (callId == INVALID_ID || characterId == INVALID_ID) {
            return null
        }
        return IncomingCallArgs(
            callId = callId,
            characterId = characterId,
            characterName = getStringExtra(EXTRA_CHARACTER_NAME).orEmpty(),
            characterImageUrl = getStringExtra(EXTRA_CHARACTER_IMAGE_URL),
            autoAccept = getBooleanExtra(EXTRA_AUTO_ACCEPT, false),
        )
    }

    companion object {
        private const val EXTRA_CALL_ID =
            "kr.co.call.callfromai.incomingcall.extra.CALL_ID"
        private const val EXTRA_CHARACTER_ID =
            "kr.co.call.callfromai.incomingcall.extra.CHARACTER_ID"
        private const val EXTRA_CHARACTER_NAME =
            "kr.co.call.callfromai.incomingcall.extra.CHARACTER_NAME"
        private const val EXTRA_CHARACTER_IMAGE_URL =
            "kr.co.call.callfromai.incomingcall.extra.CHARACTER_IMAGE_URL"
        private const val EXTRA_AUTO_ACCEPT =
            "kr.co.call.callfromai.incomingcall.extra.AUTO_ACCEPT"
        private const val INVALID_ID = -1L

        fun createIntent(
            context: Context,
            call: IncomingCall,
            autoAccept: Boolean = false,
        ): Intent =
            Intent(context, IncomingCallActivity::class.java).apply {
                putExtra(EXTRA_CALL_ID, call.callId)
                putExtra(EXTRA_CHARACTER_ID, call.characterId)
                putExtra(EXTRA_CHARACTER_NAME, call.characterName)
                putExtra(EXTRA_CHARACTER_IMAGE_URL, call.characterImageUrl)
                putExtra(EXTRA_AUTO_ACCEPT, autoAccept)
            }
    }
}

// 통화 걸려오는 화면에서 들고 있어야 할 데이터
private data class IncomingCallArgs(
    val callId: Long,
    val characterId: Long,
    val characterName: String,
    val characterImageUrl: String?,
    val autoAccept: Boolean,
)

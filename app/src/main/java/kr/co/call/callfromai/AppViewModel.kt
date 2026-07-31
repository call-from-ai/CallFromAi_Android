package kr.co.call.callfromai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kr.co.call.callfromai.intent.AppIntent
import kr.co.call.callfromai.sideeffect.AppSideEffect
import kr.co.call.callfromai.state.AppState
import kr.co.call.data.push.PushTokenManager
import kr.co.call.datastore.TokenDataStore
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber

/**
 * 앱 전역 VM
 * - 로그인 세션이 있으면 기동 시 FCM 토큰을 서버에 재등록한다
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val pushTokenManager: PushTokenManager,
    private val tokenDataStore: TokenDataStore,
) : ViewModel(),
    ContainerHost<AppState, AppSideEffect> {

    override val container: Container<AppState, AppSideEffect> = container(
        initialState = AppState(),
    )

    init {
        registerPushTokenIfLoggedIn()
    }

    fun handleIntent(intent: AppIntent) {
        when (intent) {
            else -> Unit
        }
    }

    /**
     * 이미 로그인된 채 앱만 재실행해도 서버 FCM 토큰을 최신으로 맞춘다.
     */
    private fun registerPushTokenIfLoggedIn() {
        viewModelScope.launch {
            if (tokenDataStore.getAccessToken().isNullOrBlank()) {
                Timber.d("FCM 재등록 스킵: 미로그인")
                return@launch
            }
            pushTokenManager.registerCurrentDevice()
        }
    }
}

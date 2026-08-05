package kr.co.call.callfromai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kr.co.call.callfromai.intent.AppIntent
import kr.co.call.callfromai.sideeffect.AppSideEffect
import kr.co.call.callfromai.state.AppAuthState
import kr.co.call.callfromai.state.AppState
import kr.co.call.data.push.PushTokenManager
import kr.co.call.datastore.AuthSessionManager
import kr.co.call.datastore.TokenDataStore
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.coroutines.cancellation.CancellationException
import timber.log.Timber

@HiltViewModel
class AppViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val authSessionManager: AuthSessionManager,
    private val pushTokenManager: PushTokenManager,
) : ViewModel(), ContainerHost<AppState, AppSideEffect> {

    override val container: Container<AppState, AppSideEffect> = container(
        initialState = AppState(),
    )

    init {
        checkAuthState()
        observeSessionExpiration()
        registerPushTokenIfLoggedIn()
    }

    private fun checkAuthState() = intent {
        val splashStartTime = System.currentTimeMillis()

        val storedTokens = try {
            tokenDataStore.getTokens()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Timber.e(exception, "저장된 토큰을 읽지 못했습니다.")
            null
        }

        val elapsedTime =
            System.currentTimeMillis() - splashStartTime

        val remainingTime =
            SPLASH_DURATION_MILLIS - elapsedTime

        if (remainingTime > 0L) {
            delay(remainingTime)
        }

        reduce {
            val authState=
                if(
                    storedTokens == null ||
                    storedTokens.accessToken.isNullOrBlank()
                ){
                    AppAuthState.Unauthenticated
                } else {
                    AppAuthState.Authenticated(
                        needsOnboarding=storedTokens.needsOnboarding,
                    )
                }
            state.copy(
                authState=authState,
            )
        }
    }

    private fun observeSessionExpiration() = intent {
        authSessionManager.sessionExpired.collect {
            postSideEffect(AppSideEffect.NavigateToLogin)
        }
    }

    fun handleIntent(appIntent: AppIntent) {
        when (appIntent) {
            else -> Unit
        }
    }

    // 로그인 상태면 FCM 토큰 재등록
    private fun registerPushTokenIfLoggedIn() {
        viewModelScope.launch {
            if (tokenDataStore.getTokens().accessToken.isNullOrBlank()) {
                Timber.d("FCM 재등록 스킵: 미로그인")
                return@launch
            }

            pushTokenManager.registerCurrentDevice()
        }
    }

    private companion object {
        const val SPLASH_DURATION_MILLIS = 3_000L
    }
}
package kr.co.call.callfromai

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kr.co.call.callfromai.intent.AppIntent
import kr.co.call.callfromai.sideeffect.AppSideEffect
import kr.co.call.callfromai.state.AppAuthState
import kr.co.call.callfromai.state.AppState
import kr.co.call.datastore.AuthSessionManager
import kr.co.call.datastore.TokenDataStore
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class AppViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val authSessionManager: AuthSessionManager,
) : ViewModel(), ContainerHost<AppState, AppSideEffect> {

    override val container: Container<AppState, AppSideEffect> = container(
        initialState = AppState(),
    )

    init {
        checkAuthState()
        observeSessionExpiration()
    }

    private fun checkAuthState() = intent {
        val splashStartTime = System.currentTimeMillis()

        val accessToken = try {
            tokenDataStore.getTokens().accessToken
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
            state.copy(
                authState = if (accessToken.isNullOrBlank()) {
                    AppAuthState.Unauthenticated
                } else {
                    AppAuthState.Authenticated
                },
            )
        }
    }

    private fun observeSessionExpiration()=intent{
        authSessionManager.sessionExpired.collect{
            postSideEffect(AppSideEffect.NavigateToLogin)
        }
    }

    fun handleIntent(appIntent: AppIntent) {
        when (appIntent) {
            else -> Unit
        }
    }
    private companion object{
        const val SPLASH_DURATION_MILLIS=3_000L
    }
}

package kr.co.call.callfromai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kr.co.call.callfromai.incomingcall.IncomingCallRouter
import kr.co.call.callfromai.incomingcall.IncomingCallStore
import kr.co.call.callfromai.intent.AppIntent
import kr.co.call.callfromai.sideeffect.AppSideEffect
import kr.co.call.callfromai.state.AppAuthState
import kr.co.call.callfromai.state.AppState
import kr.co.call.data.push.PushTokenManager
import kr.co.call.datastore.AuthSessionManager
import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.repository.CallControlRepository
import kr.co.call.domain.repository.MyPageRepository
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.coroutines.cancellation.CancellationException
import timber.log.Timber

@HiltViewModel
class AppViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val myPageRepository: MyPageRepository,
    private val authSessionManager: AuthSessionManager,
    private val pushTokenManager: PushTokenManager,
    private val incomingCallStore: IncomingCallStore,
    private val incomingCallRouter: IncomingCallRouter,
    private val callControlRepository: CallControlRepository,
) : ViewModel(), ContainerHost<AppState, AppSideEffect> {

    override val container: Container<AppState, AppSideEffect> = container(
        initialState = AppState(),
    )

    init {
        checkAuthState()
        observeSessionExpiration()
        observeIncomingCall()
        registerPushTokenIfLoggedIn()
        checkPendingIncomingCall()
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

        val hasAccessToken=
            storedTokens?.accessToken.isNullOrBlank().not()
        val serverNeedsOnboarding=
            if(hasAccessToken){
                myPageRepository.getNeedsOnboarding()
                    .onFailure{error ->
                        if (error is CancellationException){
                            throw error
                        }
                        Timber.e(
                            error, "서버 온보딩 상태 조회 실패",
                        )
                    }
                    .getOrNull()
            }else {
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
                        needsOnboarding=serverNeedsOnboarding
                            ?: storedTokens.needsOnboarding,
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

    private fun observeIncomingCall() = intent {
        incomingCallStore.incomingCall.collect { call ->
            reduce { state.copy(incomingCall = call) }
        }
    }

    // 전화 push 유실되었을 경우 방지용 착신 대기중인 전화 1회성 조회 (콜드 스타트 + onResume에서 호출)
    fun checkPendingIncomingCall() {
        viewModelScope.launch {
            if (tokenDataStore.getTokens().accessToken.isNullOrBlank()) {
                return@launch
            }
            try {
                callControlRepository.getIncomingCall()?.let { call ->
                    incomingCallRouter.route(call)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Timber.e(exception, "대기 중인 착신 조회 실패")
            }
        }
    }

    fun handleIntent(appIntent: AppIntent) {
        when (appIntent) {
            is AppIntent.LoginSucceeded -> onLoginSucceeded(
                needsOnboarding=appIntent.needsOnboarding,
            )
            AppIntent.LogoutSucceeded ->onLogoutSucceeded()
        }
    }
    private fun onLoginSucceeded(
        needsOnboarding: Boolean,
    ) = intent {
        Timber.d(
            "인증 상태 변경: Unauthenticated -> Authenticated, needsOnboarding=%s",
            needsOnboarding,
        )

        reduce {
            state.copy(
                authState = AppAuthState.Authenticated(
                    needsOnboarding = needsOnboarding,
                ),
            )
        }
    }

    private fun onLogoutSucceeded() = intent {
        Timber.d(
            "로그아웃 분기: %s -> Unauthenticated",
            state.authState,
        )

        reduce {
            state.copy(
                authState = AppAuthState.Unauthenticated,
            )
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

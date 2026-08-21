package kr.co.call.callfromai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kr.co.call.callfromai.incomingcall.IncomingCallRouter
import kr.co.call.callfromai.incomingcall.IncomingCallStore
import kr.co.call.callfromai.incomingchat.IncomingChat
import kr.co.call.callfromai.incomingchat.IncomingChatStore
import kotlinx.coroutines.flow.first
import kr.co.call.callfromai.intent.AppIntent
import kr.co.call.callfromai.sideeffect.AppSideEffect
import kr.co.call.callfromai.state.AppAuthState
import kr.co.call.callfromai.state.AppState
import kr.co.call.data.push.PushTokenManager
import kr.co.call.datastore.AuthSessionManager
import kr.co.call.datastore.TokenDataStore
import kr.co.call.domain.repository.CallControlRepository
import kr.co.call.domain.repository.ChatRepository
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
    private val incomingChatStore: IncomingChatStore,
    private val incomingCallRouter: IncomingCallRouter,
    private val callControlRepository: CallControlRepository,
    private val chatRepository: ChatRepository,
) : ViewModel(), ContainerHost<AppState, AppSideEffect> {

    override val container: Container<AppState, AppSideEffect> = container(
        initialState = AppState(),
    )

    init {
        checkAuthState()
        observeSessionExpiration()
        observeIncomingCall()
        observeIncomingChat()
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
        val serverRequirements=
            if(hasAccessToken){
                myPageRepository.getAuthRequirements()
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

        val authState =
            if (
                storedTokens == null ||
                storedTokens.accessToken.isNullOrBlank()
            ) {
                AppAuthState.Unauthenticated
            } else {
                AppAuthState.Authenticated(
                    needsOnboarding =
                        serverRequirements?.needsOnboarding
                            ?: storedTokens.needsOnboarding,
                    needsTermsAgreement =
                        serverRequirements?.needsTermsAgreement
                            ?: storedTokens.needsTermsAgreement,
                )
            }

        reduce {
            state.copy(
                authState = authState,
            )
        }

        // cold start 시 push로 진입한 경우 인증 확인 후 채팅방으로 이동
        if (authState is AppAuthState.Authenticated) {
            pendingChatRoomId?.let { roomId ->
                pendingChatRoomId = null
                postSideEffect(AppSideEffect.NavigateToChatRoom(roomId))
            }
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

    private fun observeIncomingChat() = intent {
        incomingChatStore.incomingChatFlow.collect { rawChat ->
            // profileImageUrl을 채팅방 헤더 API로 보완한 뒤 상태에 반영
            // API 완료 전까지 incomingChat은 null이므로 다이얼로그 표시가 지연됨
            val profileImageUrl = chatRepository
                .getChatRoomHeader(rawChat.chatRoomId)
                .onFailure { Timber.e(it, "채팅방 헤더 조회 실패 roomId=${rawChat.chatRoomId}") }
                .getOrNull()
                ?.characterImageUrl

            reduce {
                state.copy(
                    incomingChat = IncomingChat(
                        chatRoomId = rawChat.chatRoomId,
                        characterName = rawChat.characterName,
                        message = rawChat.message,
                        profileImageUrl = profileImageUrl,
                    ),
                )
            }

            // 현재 incomingChat이 처리(다이얼로그 표시 및 사용자 확인)될 때까지 다음 알림 처리를 대기
            // incomingChat이 null이 되는 시점까지 collect가 suspend되므로,
            // Channel에 대기 중인 다음 알림은 현재 알림 처리가 끝난 후 순차적으로 소비됨
            container.stateFlow.first { it.incomingChat == null }
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

    // cold start 시 auth 확인 전에 push가 들어올 수 있어 pending으로 보관
    // 다른 분들과 충돌날까봐 이렇게 했서용..
    private var pendingChatRoomId: Long? = null

    fun handleIntent(appIntent: AppIntent) {
        when (appIntent) {
            is AppIntent.LoginSucceeded -> onLoginSucceeded(
                needsOnboarding=appIntent.needsOnboarding,
                needsTermsAgreement=appIntent.needsTermsAgreement,
            )
            AppIntent.LogoutSucceeded -> onLogoutSucceeded()
            is AppIntent.OnChatPushTapped -> onChatPushTapped(appIntent.chatRoomId)
            AppIntent.OnNoticePushTapped -> onNoticePushTapped()
            AppIntent.DismissIncomingChat -> onDismissIncomingChat()
        }
    }

    private fun onDismissIncomingChat() = intent {
        // 다이알로그 소비 완료시킴.
        reduce { state.copy(incomingChat = null) }
    }

    private fun onChatPushTapped(chatRoomId: Long) = intent {
        if (state.authState is AppAuthState.Authenticated) {
            postSideEffect(AppSideEffect.NavigateToChatRoom(chatRoomId))
        } else {
            // auth 확인 중(Loading)이거나 미인증이면 보관
            pendingChatRoomId = chatRoomId
        }
    }

    private fun onNoticePushTapped() = intent {
        if (state.authState is AppAuthState.Authenticated) {
            postSideEffect(AppSideEffect.NavigateToHome)
        }
    }
    private fun onLoginSucceeded(
        needsOnboarding: Boolean,
        needsTermsAgreement: Boolean,
    ) = intent {
        tokenDataStore.setNeedsTermsAgreement(needsTermsAgreement)
        Timber.d(
            "인증 상태 변경: needsOnboarding=%s, needsTermsAgreement=%s",
            needsOnboarding,
            needsTermsAgreement,
        )

        reduce {
            state.copy(
                authState = AppAuthState.Authenticated(
                    needsOnboarding = needsOnboarding,
                    needsTermsAgreement =needsTermsAgreement,
                ),
            )
        }

        // 인증이 완료되기 전에 푸시를 눌렀다면 pending으로 저장해 둔 채팅방으로 이동
        // 한 번만 처리되도록 pending 값을 먼저 비운 뒤 네비게이션 SideEffect를 발생시킨다.
        pendingChatRoomId?.let { roomId ->
            pendingChatRoomId = null
            postSideEffect(AppSideEffect.NavigateToChatRoom(roomId))
        }
    }

    private fun onLogoutSucceeded() = intent {
        tokenDataStore.clearTokens()
        Timber.d(
            "로그아웃 분기: %s -> Unauthenticated",
            state.authState,
        )

        reduce {
            state.copy(
                authState = AppAuthState.Unauthenticated,
                incomingChat = null,
            )
        }
        postSideEffect(AppSideEffect.NavigateToLogin)
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

package kr.co.call.callfromai

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kr.co.call.api.AgreementDetailNavKey
import kr.co.call.api.AgreementNavKey
import kr.co.call.api.CallActiveNavKey
import kr.co.call.api.CallRecordNavKey
import kr.co.call.api.CallSendingNavKey
import kr.co.call.api.CallTimeManagementNavKey
import kr.co.call.api.CharacterManagementNavKey
import kr.co.call.api.ChatRoomNavKey
import kr.co.call.api.ChattingNavKey
import kr.co.call.api.DisturbTimeNavKey
import kr.co.call.api.EditCharacterNavKey
import kr.co.call.api.EditProfileNavKey
import kr.co.call.api.FaqNavKey
import kr.co.call.api.HomeNavKey
import kr.co.call.api.LoginNavKey
import kr.co.call.api.ManagerChatRoomNayKey
import kr.co.call.api.MyPageNavKey
import kr.co.call.api.ProfileNavKey
import kr.co.call.api.SubscriptionNavKey
import kr.co.call.api.Onboarding1NavKey
import kr.co.call.api.Onboarding2NavKey
import kr.co.call.api.OnboardingFlowMode
import kr.co.call.api.Onboarding3NavKey
import kr.co.call.api.Onboarding4NavKey
import kr.co.call.api.Onboarding5NavKey
import kr.co.call.api.Onboarding6NavKey
import kr.co.call.api.TermNavKey
import kr.co.call.callfromai.incomingchat.IncomingChat
import kr.co.call.callfromai.incomingchat.IncomingChatDialog
import kr.co.call.callfromai.intent.AppIntent
import kr.co.call.callfromai.sideeffect.AppSideEffect
import kr.co.call.callfromai.ui.MainBottomBar
import kr.co.call.callfromai.ui.MainTab
import kr.co.call.callfromai.util.toMainTab
import kr.co.call.callfromai.state.AppAuthState
import kr.co.call.designsystem.component.LocalBottomBarPadding
import kr.co.call.domain.model.call.IncomingCall
import kr.co.call.impl.entry.chattingEntry
import kr.co.call.impl.entry.callEntry
import kr.co.call.impl.entry.homeEntry
import kr.co.call.impl.entry.loginEntry
import kr.co.call.impl.entry.myPageEntry
import kr.co.call.impl.entry.onboardingEntry
import kr.co.call.impl.screen.LandingScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kr.co.call.impl.screen.IncomingCallDialogRoute

/**
 * 애플리케이션 화면 내비게이션의 메인 진입점입니다.
 *
 * 단일 백스택으로 로그인/온보딩/탭 화면을 모두 관리하며,
 * 현재 백스택 최상단 키를 기준으로 BottomBar 표시 여부를 결정합니다.
 *
 * @param incomingCall 현재 앱 내부에 표시할 착신 정보
 * @param onClearIncomingCall 처리가 끝난 착신 상태를 제거하는 콜백
 * @param modifier 루트 [Box]에 적용할 [Modifier]
 */
@Composable
fun AppScreen(
    viewModel: AppViewModel,
    incomingCall: IncomingCall?,
    onClearIncomingCall: (callId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.collectAsState()

    when (val authState =state.authState) {
        AppAuthState.Loading -> {
            LandingScreen(
                modifier = modifier.fillMaxSize(),
            )
        }

        is AppAuthState.Authenticated -> {
            MainAppContent(
                startKey = when {
                    authState.needsTermsAgreement -> LoginNavKey
                    authState.needsOnboarding-> Onboarding1NavKey
                    else -> HomeNavKey
                    },
                agreementKey=if(authState.needsTermsAgreement){
                    AgreementNavKey(
                        needsOnboarding=authState.needsOnboarding,
                    )
                }
                    else {
                        null
                },
                viewModel = viewModel,
                incomingCall = incomingCall,
                onClearIncomingCall = onClearIncomingCall,
                incomingChat = state.incomingChat,
                modifier = modifier,
            )
        }

        AppAuthState.Unauthenticated -> {
            MainAppContent(
                startKey = LoginNavKey,
                viewModel = viewModel,
                incomingCall = incomingCall,
                onClearIncomingCall = onClearIncomingCall,
                incomingChat = state.incomingChat,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun MainAppContent(
    startKey: NavKey,
    agreementKey: AgreementNavKey? = null,
    viewModel: AppViewModel,
    incomingCall: IncomingCall?,
    onClearIncomingCall: (callId: Long) -> Unit,
    incomingChat: IncomingChat?,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(startKey)

    val appNavigator = remember(backStack) {
        AppNavigator(backStack)
    }
    LaunchedEffect(agreementKey) {
        if (
            agreementKey != null &&
            backStack.lastOrNull() != agreementKey
        ) {
            appNavigator.navigate(agreementKey)
        }
    }
    val currentKey = backStack.lastOrNull()
    // API 실패 메시지 표시에 사용할 현재 화면 Context
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            AppSideEffect.NavigateToLogin -> {
                appNavigator.replaceAll(LoginNavKey)
            }
            is AppSideEffect.NavigateToChatRoom -> {
                appNavigator.navigateToChatRoom(sideEffect.chatRoomId)
            }
            AppSideEffect.NavigateToHome -> {
                appNavigator.replaceAll(HomeNavKey)
            }
        }
    }

    val showBottomBar = when (currentKey) {
        is HomeNavKey,
        is ChattingNavKey,
        is MyPageNavKey,
        is FaqNavKey,
        is TermNavKey -> true

        is ManagerChatRoomNayKey -> false

        else -> false
    }

    val currentTab = currentKey?.toMainTab() ?: MainTab.HOME
    // Compose Density를 통해 px 값을 dp로 변환하기 위해 사용
    val density = LocalDensity.current

    // BottomBar의 실제 렌더링 높이(px)를 저장
    var bottomBarHeightPx by remember {
        mutableIntStateOf(0)
    }

    // 저장된 BottomBar 높이를 화면 padding에 사용할 dp 값으로 변환
    val bottomBarPadding = remember(bottomBarHeightPx) {
        with(density) {
            bottomBarHeightPx.toDp()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                MainBottomBar(
                    currentTab = currentTab,
                    onTabSelected = appNavigator::navigateToTab,
                    modifier = Modifier.onSizeChanged {
                        bottomBarHeightPx = it.height
                    },
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
            .only(
                WindowInsetsSides.Bottom +
                        WindowInsetsSides.Horizontal,
            )
            .exclude(WindowInsets.ime),
    ) { padding ->
        CompositionLocalProvider(
            LocalBottomBarPadding provides if (showBottomBar) {
                bottomBarPadding
            } else {
                0.dp
            },
        ) {
            NavDisplay(
                backStack = backStack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    loginEntry(
                        navigateToOnboarding = {needsOnboarding,needsTermsAgreement,->
                            viewModel.handleIntent(
                                AppIntent.LoginSucceeded(
                                    needsOnboarding = needsOnboarding,
                                    needsTermsAgreement = needsTermsAgreement,
                            ),
                            )
                            appNavigator.replaceAll(Onboarding1NavKey)
                        },
                        navigateToHome = {needsOnboarding,needsTermsAgreement, ->
                        viewModel.handleIntent(
                                AppIntent.LoginSucceeded(
                                    needsOnboarding = needsOnboarding,
                                    needsTermsAgreement = needsTermsAgreement,
                                    ),
                            )
                            appNavigator.replaceAll(HomeNavKey)
                        },
                        navigateToAgreement = {needsOnboarding ->
                            appNavigator.navigate(
                                AgreementNavKey(
                                    needsOnboarding = needsOnboarding,
                                ),
                            )
                        },
                        navigateToAgreementDetail = { term ->
                            appNavigator.navigate(
                                AgreementDetailNavKey(
                                    termId = term.termId,
                                    title = term.title,
                                    content = term.content,
                                ),
                            )
                        },
                        navigateAfterAgreement = { needsOnboarding ->
                            viewModel.handleIntent(
                                AppIntent.LoginSucceeded(
                                    needsOnboarding = needsOnboarding,
                                    needsTermsAgreement = false
                                ),
                            )

                            if (needsOnboarding) {
                                appNavigator.replaceAll(Onboarding1NavKey)
                            } else {
                                appNavigator.replaceAll(HomeNavKey)
                            }
                        },
                        onBack = {
                            appNavigator.popBackStack()
                        },
                    )

                    onboardingEntry(
                        onOnboarding1Next = {
                            appNavigator.navigate(
                                Onboarding2NavKey(mode = OnboardingFlowMode.FIRST_ONBOARDING),
                            )
                        },
                        onBackFromOnboarding1 = {
                            viewModel.handleIntent(AppIntent.LogoutSucceeded)
                            appNavigator.replaceAll(LoginNavKey)
                        },
                        onBackFromOnboarding2 = {
                            appNavigator.popBackStack()
                        },
                        onOnboarding2Next = {
                            appNavigator.navigate(Onboarding3NavKey)
                        },
                        onBackFromOnboarding3 = {
                            appNavigator.popBackStack()
                        },
                        onOnboarding3Next = {
                            appNavigator.navigate(Onboarding4NavKey)
                        },
                        onBackFromOnboarding4 = {
                            appNavigator.popBackStack()
                        },
                        onOnboarding4Next = {
                            appNavigator.navigate(Onboarding5NavKey)
                        },
                        onBackFromOnboarding5 = {
                            appNavigator.popBackStack()
                        },
                        onOnboarding5Next = {
                            appNavigator.navigate(Onboarding6NavKey)
                        },
                        onAdditionalCharacterCreated = {
                            // 캐릭터 추가 완료 -> 전화 화면 스킵, 홈으로
                            appNavigator.replaceAll(HomeNavKey)
                        },
                        onOnboarding6CallNow = { characterId, characterName, characterImageUrl ->
                            // 온보딩 백스택은 정리하고 홈을 기반으로 통화 화면을 쌓아,
                            // 통화 종료 시 popBackStack()이 홈으로 돌아가도록 함
                            appNavigator.replaceAll(HomeNavKey)
                            appNavigator.navigate(
                                CallSendingNavKey(
                                    characterId = characterId,
                                    characterName = characterName,
                                    characterImageUrl = characterImageUrl,
                                ),
                            )
                        },
                        onOnboarding6CallLater = {
                            appNavigator.replaceAll(HomeNavKey)
                        },
                    )

                    homeEntry(
                        navigateToCall = { characterId, characterName, characterImageUrl ->
                            appNavigator.navigate(
                                CallSendingNavKey(
                                    characterId = characterId,
                                    characterName = characterName,
                                    characterImageUrl = characterImageUrl,
                                ),
                            )
                        },
                        navigateToCallRecord = { callId ->
                            appNavigator.navigate(
                                CallRecordNavKey(
                                    callId = callId,
                                ),
                            )
                        },
                        navigateToCharacterOnboarding = {
                            appNavigator.navigate(
                                Onboarding2NavKey(
                                    mode = OnboardingFlowMode.ADD_CHARACTER,
                                    resetToken = System.currentTimeMillis(),
                                ),
                            )
                        },
                        onCallRecordBack = {
                            appNavigator.popBackStack()
                        },
                    )

                    callEntry(
                        onIncomingAccepted = { callId, characterId, characterName, characterImageUrl ->
                            appNavigator.replaceTop(
                                CallActiveNavKey(
                                    callId = callId,
                                    characterId = characterId,
                                    isIncoming = true,
                                    characterName = characterName,
                                    characterImageUrl = characterImageUrl,
                                ),
                            )
                        },
                        onIncomingFinished = {
                            appNavigator.popBackStack()
                        },
                        onCallFinished = {
                            appNavigator.popBackStack()
                        },
                        onShowMessage = { message ->
                            Toast.makeText(
                                context,
                                message,
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                    )

                    chattingEntry(
                        navigateToChatRoom = { roomId ->
                            appNavigator.navigate(
                                ChatRoomNavKey(
                                    roomId = roomId,
                                ),
                            )
                        },
                        navigateToManagerChatRoom = {
                            appNavigator.navigate(ManagerChatRoomNayKey)
                        },
                        navigateToCall = { characterId, characterName, characterImageUrl ->
                            appNavigator.navigate(
                                CallSendingNavKey(
                                    characterId = characterId,
                                    characterName = characterName,
                                    characterImageUrl = characterImageUrl,
                                ),
                            )
                        },
                        onBack = {
                            appNavigator.popBackStack()
                        },
                    )

                    myPageEntry(
                        navigateToLogin = {
                            viewModel.handleIntent(AppIntent.LogoutSucceeded)
                            appNavigator.replaceAll(LoginNavKey)
                        },
                        navigateToFaq = {
                            appNavigator.navigate(FaqNavKey)
                        },
                        navigateToTerms = {
                            appNavigator.navigate(TermNavKey)
                        },
                        navigateToCharacterManagement = {
                            appNavigator.navigate(CharacterManagementNavKey)
                        },
                        navigateToProfile = {
                            appNavigator.navigate(ProfileNavKey)
                        },
                        navigateToEditProfile = {
                            appNavigator.navigate(EditProfileNavKey)
                        },
                        navigateToSubscription = {
                            appNavigator.navigate(SubscriptionNavKey)
                        },
                        navigateToDisturbTime = {
                            appNavigator.navigate(DisturbTimeNavKey)
                        },
                        navigateToCallTimeManagement = {
                            appNavigator.navigate(CallTimeManagementNavKey)
                        },
                        navigateToEditCharacter = { characterId ->
                            appNavigator.navigate(EditCharacterNavKey(characterId))
                        },
                        navigateToAddCharacter = {
                            // 온보딩 2로 바로 진입
                            appNavigator.navigate(
                                Onboarding2NavKey(
                                    mode = OnboardingFlowMode.ADD_CHARACTER,
                                    resetToken = System.currentTimeMillis(),
                                ),
                            )
                        },
                        onBack = {
                            appNavigator.popBackStack()
                        },
                    )
                },
            )
        }
    }

    /**
     * 통화 걸려왔을 때의 이동 처리
     */
    incomingCall?.let { call ->
        IncomingCallDialogRoute(
            call = call,
            onNavigateToChatRoom = { roomId ->
                // 채팅방 식별자가 있을 때만 이동
                roomId?.let { id ->
                    appNavigator.navigate(
                        ChatRoomNavKey(roomId = id),
                    )
                }
                onClearIncomingCall(call.callId)
            },
            onAccepted = { callId, characterId, characterName, characterImageUrl ->
                appNavigator.navigate(
                    CallActiveNavKey(
                        callId = callId,
                        characterId = characterId,
                        isIncoming = true,
                        characterName = characterName,
                        characterImageUrl = characterImageUrl,
                    ),
                )
                onClearIncomingCall(callId)
            },
            onRejected = { callId ->
                onClearIncomingCall(callId)
            },
            onShowMessage = { message ->
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT,
                ).show()
            },
        )
    }

    /**
     * 포그라운드에서 채팅 FCM 수신 시 인앱 다이얼로그 표시
     * (profileImageUrl 매핑이 완료된 이후에 state.incomingChat이 세팅되므로 API 완료 전까지 표시 지연됨)
     */
    incomingChat?.let { chat ->
        IncomingChatDialog(
            characterName = chat.characterName,
            message = chat.message,
            profileImageUrl = chat.profileImageUrl,
            onNavigateToChatRoom = {
                appNavigator.navigate(ChatRoomNavKey(roomId = chat.chatRoomId))
                viewModel.handleIntent(AppIntent.DismissIncomingChat)
            },
            onDismiss = {
                viewModel.handleIntent(AppIntent.DismissIncomingChat)
            },
        )
    }
}

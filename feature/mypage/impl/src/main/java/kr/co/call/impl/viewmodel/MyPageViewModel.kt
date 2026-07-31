package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kr.co.call.domain.repository.MyPageRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.auth.KakaoLogoutManager
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
    private val kakaoLogoutManager: KakaoLogoutManager,
) : ViewModel(), ContainerHost<MyPageState, MyPageSideEffect> {

    override val container: Container<MyPageState, MyPageSideEffect> = container(
        initialState = MyPageState()
    ) {
        loadMyProfile()
    }

    fun handleIntent(intent: MyPageIntent) {
        when (intent) {
            is MyPageIntent.ClickProfile -> navigate(MyPageSideEffect.NavigateToProfileDetail)
            is MyPageIntent.ClickChargeTicket -> navigate(MyPageSideEffect.NavigateToChargeTicket)
            is MyPageIntent.ClickPurchaseTicket -> navigate(MyPageSideEffect.NavigateToPurchaseTicket)
            is MyPageIntent.ClickHistoryTicket -> navigate(MyPageSideEffect.NavigateToTicketHistory)
            is MyPageIntent.ClickCharacterManagement -> navigate(MyPageSideEffect.NavigateToCharacterManagement)
            is MyPageIntent.ClickFaq -> navigate(MyPageSideEffect.NavigateToFaq)
            is MyPageIntent.ClickInquiry -> navigate(MyPageSideEffect.NavigateToInquiry)
            is MyPageIntent.ClickTerms -> navigate(MyPageSideEffect.NavigateToTerms)
            is MyPageIntent.ClickLogout -> navigate(MyPageSideEffect.ShowLogoutConfirmDialog)
            is MyPageIntent.ClickDeleteAccount -> navigate(MyPageSideEffect.ShowDeleteAccountConfirmDialog)
            is MyPageIntent.ConfirmLogout -> logout()
            is MyPageIntent.ConfirmDeleteAccount -> deleteAccount()
        }
    }

    private fun loadMyProfile() = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading) }
        myPageRepository.getMyProfile()
            .onSuccess { profile ->
                reduce {
                    state.copy(
                        profileImageUrl = profile.profileImageUrl,
                        nickname = profile.nickname,
                        tier = profile.tier,
                        remainingTicketCount = profile.remainingTicketCount,
                        appVersion = profile.appVersion,
                        loadStatus = LoadStatus.Idle,
                    )
                }
            }
            .onFailure { e ->
                if (e is CancellationException) throw e
                reduce { state.copy(loadStatus = LoadStatus.Error(e.message ?: "내 프로필 불러오기 실패")) }
            }
    }

    private fun logout() = intent {
        if (state.authStatus== LoadStatus.Loading)return@intent
        reduce{
            state.copy(authStatus = LoadStatus.Loading)
        }
        val serverLogoutError = myPageRepository.logout().exceptionOrNull()
        if (serverLogoutError != null) {
            if (serverLogoutError is CancellationException) throw serverLogoutError

            reduce {
                state.copy(authStatus = LoadStatus.Idle)
            }
            postSideEffect(
                MyPageSideEffect.ShowMessage(
                    message = serverLogoutError.message
                        ?: "로그아웃에 실패했습니다.",
                ),
            )
            return@intent
        }

        val kakaoLogoutError = kakaoLogoutManager.logout().exceptionOrNull()
        if (kakaoLogoutError != null) {
            if (kakaoLogoutError is CancellationException) throw kakaoLogoutError

            Timber.w(kakaoLogoutError, "카카오 SDK 로그아웃 실패")
            reduce {
                state.copy(authStatus = LoadStatus.Idle)
            }
            postSideEffect(
                MyPageSideEffect.ShowMessage(
                    message = "카카오 로그아웃에 실패했습니다. 다시 시도해 주세요.",
                ),
            )
            return@intent
        }

        postSideEffect(MyPageSideEffect.NavigateToLogin)
    }

    private fun deleteAccount() = intent {
        if (state.authStatus== LoadStatus.Loading) return@intent

        reduce{
            state.copy(authStatus = LoadStatus.Loading)
        }
        myPageRepository.deleteAccount()
            .onSuccess {
                kakaoLogoutManager.logout()
                    .onFailure {error->
                        Timber.w(error, "탈퇴 후 카카오 로그아웃 실패")
                    }
                postSideEffect(MyPageSideEffect.NavigateToLogin)
            }.onFailure { error ->
            if (error is CancellationException) throw error
            reduce {
                state.copy(authStatus = LoadStatus.Idle)
            }
            postSideEffect(
                MyPageSideEffect.ShowMessage(
                    message=error.message
                        ?:"회원 탈퇴에 실패했습니다.",
                ),
            )
        }
    }

    private fun navigate(effect: MyPageSideEffect) = intent {
        postSideEffect(effect)
    }
}

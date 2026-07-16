package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.repository.MyPageRepository
import kr.co.call.domain.util.LoadStatus
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
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
        runCatching { myPageRepository.getMyProfile() }
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
                reduce { state.copy(loadStatus = LoadStatus.Error(e.message ?: "내 프로필 불러오기 실패")) }
            }
    }

    private fun logout() = intent {
        myPageRepository.logout()
        postSideEffect(MyPageSideEffect.NavigateToLogin)
    }

    private fun deleteAccount() = intent {
        myPageRepository.deleteAccount()
        postSideEffect(MyPageSideEffect.NavigateToLanding)
    }

    private fun navigate(effect: MyPageSideEffect) = intent {
        postSideEffect(effect)
    }
}
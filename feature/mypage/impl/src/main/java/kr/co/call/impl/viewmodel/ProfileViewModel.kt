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
class ProfileViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
) : ViewModel(), ContainerHost<ProfileState, ProfileSideEffect> {

    override val container: Container<ProfileState, ProfileSideEffect> = container(
        initialState = ProfileState()
    ) {
        loadProfile()
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.ClickEditProfile -> navigate(ProfileSideEffect.NavigateToEditProfile)
            is ProfileIntent.ClickSubscription -> navigate(ProfileSideEffect.NavigateToSubscription)
            is ProfileIntent.ClickDoNotDisturbTime -> navigate(ProfileSideEffect.NavigateToDoNotDisturbTime)
            is ProfileIntent.ClickCallTimeManagement -> navigate(ProfileSideEffect.NavigateToCallTimeManagement)
            is ProfileIntent.ToggleAllNotification -> toggleAllNotification(intent.enabled)
            is ProfileIntent.ToggleLateNightCall -> toggleLateNightCall(intent.enabled)
        }
    }

    private fun loadProfile() = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading) }
        runCatching { myPageRepository.getMyProfile() }
            .onSuccess { profile ->
                reduce {
                    state.copy(
                        profileImageUrl = profile.profileImageUrl,
                        nickname = profile.nickname,
                        loadStatus = LoadStatus.Idle,
                    )
                }
            }
            .onFailure { e ->
                reduce { state.copy(loadStatus = LoadStatus.Error(e.message ?: "프로필 불러오기 실패")) }
            }
    }

    private fun toggleAllNotification(enabled: Boolean) = intent {
        reduce { state.copy(isAllNotificationEnabled = enabled) }
        // TODO: 서버에 알림 설정 반영
    }

    private fun toggleLateNightCall(enabled: Boolean) = intent {
        reduce { state.copy(isLateNightCallAllowed = enabled) }
        // TODO: 서버에 심야 통화 허용 설정 반영
    }

    private fun navigate(effect: ProfileSideEffect) = intent {
        postSideEffect(effect)
    }
}
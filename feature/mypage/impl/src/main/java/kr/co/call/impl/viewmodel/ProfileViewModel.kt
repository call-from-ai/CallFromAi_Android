package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kr.co.call.domain.exception.toUserMessage
import kr.co.call.domain.model.mypage.NotificationSetting
import kr.co.call.domain.repository.MyPageRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.util.formatDisturbRangeText
import kr.co.call.impl.util.parseApiLocalTime
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
) : ViewModel(), ContainerHost<ProfileState, ProfileSideEffect> {

    override val container: Container<ProfileState, ProfileSideEffect> = container(
        initialState = ProfileState(),
    ) {
        loadProfile()
        observeProfileUpdates()
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.Refresh -> loadProfile(showLoading = false)
            is ProfileIntent.ApplyLocalUpdate -> applyLocalUpdate(
                nickname = intent.nickname,
                profileImageUrl = intent.profileImageUrl,
            )
            is ProfileIntent.ClickEditProfile -> navigate(ProfileSideEffect.NavigateToEditProfile)
            is ProfileIntent.ClickSubscription -> navigate(ProfileSideEffect.NavigateToSubscription)
            is ProfileIntent.ClickDisturbTime -> navigate(ProfileSideEffect.NavigateToDisturbTime)
            is ProfileIntent.ClickCallTimeManagement ->
                navigate(ProfileSideEffect.NavigateToCallTimeManagement)
            is ProfileIntent.ToggleAllNotification -> toggleAllNotification(intent.enabled)
            is ProfileIntent.ToggleLateNightCall -> toggleLateNightCall(intent.enabled)
        }
    }

    private fun observeProfileUpdates() = intent {
        myPageRepository.profileUpdates.collect { profile ->
            reduce {
                state.copy(
                    profileImageUrl = profile.profileImageUrl,
                    nickname = profile.nickname,
                )
            }
        }
    }

    private fun applyLocalUpdate(
        nickname: String,
        profileImageUrl: String,
    ) = intent {
        reduce {
            state.copy(
                nickname = nickname,
                profileImageUrl = profileImageUrl,
            )
        }
    }

    private fun loadProfile(showLoading: Boolean = true) = intent {
        if (showLoading) {
            reduce { state.copy(loadStatus = LoadStatus.Loading) }
        }

        val profileResult = myPageRepository.getMyProfile()
        val settingResult = myPageRepository.getNotificationSetting()

        profileResult
            .onSuccess { profile ->
                reduce {
                    state.copy(
                        profileImageUrl = profile.profileImageUrl,
                        nickname = profile.nickname,
                        loadStatus = LoadStatus.Idle,
                    )
                }
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
                if (showLoading || state.nickname.isBlank()) {
                    reduce {
                        state.copy(
                            loadStatus = LoadStatus.Error(
                                error.toUserMessage(default = "프로필 불러오기 실패"),
                            ),
                        )
                    }
                }
            }

        settingResult
            .onSuccess { setting ->
                reduce { state.applyNotificationSetting(setting) }
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
            }
    }

    private fun toggleAllNotification(enabled: Boolean) = intent {
        val previous = state.isAllNotificationEnabled
        if (previous == enabled) return@intent

        reduce { state.copy(isAllNotificationEnabled = enabled) }

        myPageRepository.updateNotificationToggles(
            allNotificationEnabled = enabled,
            nightCallAllowed = null,
        ).onSuccess { setting ->
            reduce { state.applyNotificationSetting(setting) }
        }.onFailure { error ->
            if (error is CancellationException) throw error
            reduce { state.copy(isAllNotificationEnabled = previous) }
        }
    }

    private fun toggleLateNightCall(enabled: Boolean) = intent {
        val previous = state.isLateNightCallAllowed
        if (previous == enabled) return@intent

        reduce { state.copy(isLateNightCallAllowed = enabled) }

        myPageRepository.updateNotificationToggles(
            allNotificationEnabled = null,
            nightCallAllowed = enabled,
        ).onSuccess { setting ->
            reduce { state.applyNotificationSetting(setting) }
        }.onFailure { error ->
            if (error is CancellationException) throw error
            reduce { state.copy(isLateNightCallAllowed = previous) }
        }
    }

    private fun navigate(effect: ProfileSideEffect) = intent {
        postSideEffect(effect)
    }
}

private fun ProfileState.applyNotificationSetting(setting: NotificationSetting): ProfileState =
    copy(
        isAllNotificationEnabled = setting.allNotificationEnabled,
        isLateNightCallAllowed = setting.nightCallAllowed,
        disturbTimeText = formatDisturbRangeText(
            start = parseApiLocalTime(setting.doNotDisturbStart),
            end = parseApiLocalTime(setting.doNotDisturbEnd),
        ),
    )

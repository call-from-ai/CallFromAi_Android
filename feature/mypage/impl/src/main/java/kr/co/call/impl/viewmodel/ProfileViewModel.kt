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
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.Refresh -> loadProfile()
            is ProfileIntent.ClickEditProfile -> navigate(ProfileSideEffect.NavigateToEditProfile)
            is ProfileIntent.ClickSubscription -> navigate(ProfileSideEffect.NavigateToSubscription)
            is ProfileIntent.ClickDisturbTime -> navigate(ProfileSideEffect.NavigateToDisturbTime)
            is ProfileIntent.ClickCallTimeManagement ->
                navigate(ProfileSideEffect.NavigateToCallTimeManagement)
            is ProfileIntent.ToggleAllNotification -> toggleAllNotification(intent.enabled)
            is ProfileIntent.ToggleLateNightCall -> toggleLateNightCall(intent.enabled)
        }
    }

    private fun loadProfile() = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading) }

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
                reduce {
                    state.copy(
                        loadStatus = LoadStatus.Error(
                            error.toUserMessage(default = "프로필 불러오기 실패"),
                        ),
                    )
                }
            }

        settingResult
            .onSuccess { setting ->
                reduce { state.applyNotificationSetting(setting) }
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
                // 프로필은 유지하고 알림 설정만 실패 안내
                postSideEffect(
                    ProfileSideEffect.ShowMessage(
                        error.toUserMessage(default = "알림 설정을 불러오지 못했습니다."),
                    ),
                )
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
            postSideEffect(
                ProfileSideEffect.ShowMessage(
                    error.toUserMessage(default = "알림 설정 변경에 실패했습니다."),
                ),
            )
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
            postSideEffect(
                ProfileSideEffect.ShowMessage(
                    error.toUserMessage(default = "심야 통화 설정 변경에 실패했습니다."),
                ),
            )
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


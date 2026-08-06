package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject
import kr.co.call.domain.exception.toUserMessage
import kr.co.call.domain.repository.MyPageRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.util.parseApiLocalTime
import kr.co.call.impl.util.toApiTimeString
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class DisturbTimeViewModel @Inject constructor(
    private val myPageRepository: MyPageRepository,
) : ViewModel(),
    ContainerHost<DisturbTimeState, DisturbTimeSideEffect> {

    override val container: Container<DisturbTimeState, DisturbTimeSideEffect> = container(
        initialState = DisturbTimeState(),
    ) {
        loadDisturbTime()
    }

    fun handleIntent(intent: DisturbTimeIntent) {
        when (intent) {
            is DisturbTimeIntent.ClickStartTime -> openSheet(DisturbTimeSheetType.Start)
            is DisturbTimeIntent.ClickEndTime -> openSheet(DisturbTimeSheetType.End)
            is DisturbTimeIntent.SelectDraftTime -> selectDraftTime(intent.time)
            is DisturbTimeIntent.ConfirmSheet -> confirmSheet()
            is DisturbTimeIntent.DismissSheet -> dismissSheet()
            is DisturbTimeIntent.ClickComplete -> complete()
            is DisturbTimeIntent.ClickDelete -> delete()
        }
    }

    private fun loadDisturbTime() = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading) }
        myPageRepository.getNotificationSetting()
            .onSuccess { setting ->
                val start = parseApiLocalTime(setting.doNotDisturbStart)
                    ?: DisturbTimeState.CLEARED_TIME
                val end = parseApiLocalTime(setting.doNotDisturbEnd)
                    ?: DisturbTimeState.CLEARED_TIME
                // 서버에 값이 없으면 둘 다 00시로 표시
                val hasSetting = setting.doNotDisturbStart != null && setting.doNotDisturbEnd != null
                reduce {
                    state.copy(
                        startTime = if (hasSetting) start else DisturbTimeState.CLEARED_TIME,
                        endTime = if (hasSetting) end else DisturbTimeState.CLEARED_TIME,
                        loadStatus = LoadStatus.Idle,
                    )
                }
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
                reduce { state.copy(loadStatus = LoadStatus.Idle) }
                postSideEffect(
                    DisturbTimeSideEffect.ShowMessage(
                        error.toUserMessage(default = "방해 금지 시간을 불러오지 못했습니다."),
                    ),
                )
            }
    }

    private fun openSheet(type: DisturbTimeSheetType) = intent {
        val initial = when (type) {
            DisturbTimeSheetType.Start -> state.startTime
            DisturbTimeSheetType.End -> state.endTime
        }
        reduce {
            state.copy(
                openSheet = type,
                draftTime = initial,
            )
        }
    }

    private fun selectDraftTime(time: LocalTime) = intent {
        if (state.openSheet == null || state.draftTime == time) return@intent
        reduce { state.copy(draftTime = time) }
    }

    private fun confirmSheet() = intent {
        val sheet = state.openSheet ?: return@intent
        val confirmed = state.draftTime
        reduce {
            when (sheet) {
                DisturbTimeSheetType.Start -> state.copy(
                    startTime = confirmed,
                    openSheet = null,
                )
                DisturbTimeSheetType.End -> state.copy(
                    endTime = confirmed,
                    openSheet = null,
                )
            }
        }
    }

    private fun dismissSheet() = intent {
        if (state.openSheet == null) return@intent
        reduce { state.copy(openSheet = null) }
    }

    private fun complete() = intent {
        if (!state.canComplete) return@intent

        reduce { state.copy(isSaving = true) }
        myPageRepository.updateDoNotDisturb(
            startTime = state.startTime.toApiTimeString(),
            endTime = state.endTime.toApiTimeString(),
        ).onSuccess {
            reduce { state.copy(isSaving = false) }
            postSideEffect(DisturbTimeSideEffect.NavigateBack)
        }.onFailure { error ->
            if (error is CancellationException) throw error
            reduce { state.copy(isSaving = false) }
            postSideEffect(
                DisturbTimeSideEffect.ShowMessage(
                    error.toUserMessage(default = "방해 금지 시간 저장에 실패했습니다."),
                ),
            )
        }
    }

    private fun delete() = intent {
        if (state.isSaving) return@intent

        reduce { state.copy(isSaving = true) }
        myPageRepository.deleteDoNotDisturb()
            .onSuccess {
                reduce {
                    state.copy(
                        startTime = DisturbTimeState.CLEARED_TIME,
                        endTime = DisturbTimeState.CLEARED_TIME,
                        openSheet = null,
                        isSaving = false,
                    )
                }
            }
            .onFailure { error ->
                if (error is CancellationException) throw error
                reduce { state.copy(isSaving = false) }
                postSideEffect(
                    DisturbTimeSideEffect.ShowMessage(
                        error.toUserMessage(default = "방해 금지 시간 삭제에 실패했습니다."),
                    ),
                )
            }
    }
}

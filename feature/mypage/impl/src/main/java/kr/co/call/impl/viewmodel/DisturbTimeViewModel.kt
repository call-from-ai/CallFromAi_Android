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
        }
    }

    private fun loadDisturbTime() = intent {
        reduce { state.copy(loadStatus = LoadStatus.Loading) }
        myPageRepository.getNotificationSetting()
            .onSuccess { setting ->
                val start = parseApiLocalTime(setting.doNotDisturbStart)
                    ?: DisturbTimeState.DEFAULT_START
                val end = parseApiLocalTime(setting.doNotDisturbEnd)
                    ?: DisturbTimeState.DEFAULT_END
                reduce {
                    state.copy(
                        startTime = start,
                        endTime = end,
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
}

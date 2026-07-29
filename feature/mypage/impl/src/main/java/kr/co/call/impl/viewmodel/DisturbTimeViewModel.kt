package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
class DisturbTimeViewModel @Inject constructor() :
    ViewModel(),
    ContainerHost<DisturbTimeState, DisturbTimeSideEffect> {

    override val container: Container<DisturbTimeState, DisturbTimeSideEffect> = container(
        initialState = DisturbTimeState(),
    )

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
        // TODO: 서버/로컬 저장 연동 시 startTime,endTime 반영
        postSideEffect(DisturbTimeSideEffect.NavigateBack)
    }
}

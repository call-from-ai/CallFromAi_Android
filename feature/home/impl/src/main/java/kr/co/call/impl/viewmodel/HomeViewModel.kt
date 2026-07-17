package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kr.co.call.domain.repository.HomeRepository
import kr.co.call.domain.util.LoadStatus
import kr.co.call.impl.mapper.toUiState
import kr.co.call.impl.tab.HomeHistoryTab
import kr.co.call.impl.viewmodel.state.CallHistoryState
import kr.co.call.impl.viewmodel.state.HomeState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
) :
    ViewModel(),
    ContainerHost<HomeState, HomeSideEffect> {

    override val container: Container<HomeState, HomeSideEffect> = container(
        initialState = HomeState(),
    )

    init {
        loadHome()
    }

    internal fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectHistoryTab -> {
                selectHistoryTab(intent.tab)
            }

            HomeIntent.ClickCall -> {
                // 전화 화면 이동
            }

            HomeIntent.ClickNotification -> {
                // 알림 화면 이동
            }

            HomeIntent.ClickChangeTime -> {
                // 시간 변경 화면 이동
            }
        }
    }

    private fun loadHome() = intent {
        reduce {
            state.copy(loadStatus = LoadStatus.Loading)
        }

        try {
            val reservations = homeRepository.getReservations().getOrThrow()
            val callHistories = homeRepository.getCallHistories().getOrThrow()
            val summary = homeRepository.getSummary(state.summary.characterId).getOrThrow()

            reduce {
                state.copy(
                    summary = summary.toUiState(),
                    reservation = reservations.toUiState(),
                    callHistory = CallHistoryState(
                        histories = callHistories.map { history ->
                            history.toUiState()
                        },
                    ),
                    loadStatus = LoadStatus.Idle,
                )
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            reduce {
                state.copy(
                    loadStatus = LoadStatus.Error(
                        message = throwable.message ?: "홈 정보를 불러오지 못했습니다.",
                    ),
                )
            }
        }
    }

    // 기록 탭 선택
    private fun selectHistoryTab(
        tab: HomeHistoryTab,
    ) = intent {
        reduce {
            state.copy(
                selectedHistoryTab = tab,
            )
        }
    }
}

package kr.co.call.callfromai

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.callfromai.intent.AppIntent
import kr.co.call.callfromai.sideeffect.AppSideEffect
import kr.co.call.callfromai.state.AppState
import kr.co.call.datastore.AuthSessionManager
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authSessionManager: AuthSessionManager,
) : ViewModel(), ContainerHost<AppState, AppSideEffect> {

    override val container: Container<AppState, AppSideEffect> = container(
        initialState = AppState()
    )

    init {
        intent {
            authSessionManager.sessionExpired.collect {
                postSideEffect(AppSideEffect.NavigateToLogin)
            }
        }
    }

    fun handleIntent(intent: AppIntent) {
        when (intent) {
            else -> Unit
        }
    }
}

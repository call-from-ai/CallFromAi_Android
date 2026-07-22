package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kr.co.call.datastore.TokenDataStore
import kr.co.call.impl.viewmodel.state.LandingUiState
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
): ViewModel(),
    ContainerHost<LandingUiState, LandingSideEffect> {
    override val container= container <LandingUiState,LandingSideEffect>(
        initialState=LandingUiState(),
    )
    init {
        checkAutoLoginAfterSplash()
    }

    private fun checkAutoLoginAfterSplash()=intent{
        delay(SPLASH_DURATION_MILLIS)

        val accessToken = tokenDataStore.getAccessToken()
        reduce{
            state.copy(
                isCheckingAutoLogin = false,
            )
        }
        if (accessToken.isNullOrBlank()){
            postSideEffect(LandingSideEffect.NavigateToLogin)
        }else{
            postSideEffect(LandingSideEffect.NavigateToHome)
        }
    }
    private companion object {
    const val SPLASH_DURATION_MILLIS=3_000L
    }
}
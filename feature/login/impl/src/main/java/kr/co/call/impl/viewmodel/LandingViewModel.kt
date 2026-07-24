package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kr.co.call.datastore.TokenDataStore
import kr.co.call.impl.viewmodel.state.LandingUiState
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

/**
 * 앱 시작 시 저장된 Access Token을 확인해 자동 로그인을 처리한다.
 * 토큰이 없으면 로그인 화면으로, 저장돼 있으면 홈 화면으로 이동한다.
 */
@HiltViewModel
class LandingViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
): ViewModel(),
    ContainerHost<LandingUiState, LandingSideEffect> {
    override val container= container <LandingUiState,LandingSideEffect>(
        initialState=LandingUiState(),
    )
    init {
        // 랜딩 화면이 생성되면 자동 로그인 확인을 바로 시작
        checkAutoLoginAfterSplash()
    }
    /**
     * 스플래시 화면 노출 후 저장된 Access Token을 확인한다.
     * 현재는 토큰 존재 여부를 기준으로 자동 로그인 화면을 결정한다.
     */
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
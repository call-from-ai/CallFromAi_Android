package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kr.co.call.datastore.TokenDataStore
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
): ViewModel(), ContainerHost<Unit, LandingSideEffect> {
    override val container= container <Unit,LandingSideEffect>(
        initialState=Unit,
    )

    //저장된 accessToken이 있으면 자동로그인으로 홈, 없으면 로그인 화면
    fun checkAutoLogin()=intent{
        val accessToken=tokenDataStore.accessToken.first()
        if (!accessToken.isNullOrBlank()){
            postSideEffect(LandingSideEffect.NavigateToHome)
        } else {
            postSideEffect(LandingSideEffect.NavigateToLogin)
        }
    }
}
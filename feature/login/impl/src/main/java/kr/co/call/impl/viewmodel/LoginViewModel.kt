package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.repository.LoginRepository
import kr.co.call.impl.viewmodel.state.LoginState
import org.orbitmvi.orbit.ContainerHost
import javax.inject.Inject
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
): ViewModel(),
    ContainerHost<LoginState, LoginSideEffect> {
    override val container =
        container<LoginState, LoginSideEffect>(
            initialState = LoginState(),
        )

    fun loginWithKakao(
        kakaoAccessToken: String,
    ) = intent {
        reduce {
            state.copy(
                isLoading = true,
            )
        }
        runCatching {
            loginRepository.loginWithKakao(
                kakaoAccessToken = kakaoAccessToken,
            )
        }.onSuccess {
            reduce {
                state.copy(
                    isLoading = false,
                )
            }
            postSideEffect(
                LoginSideEffect.NavigateToNext,
            )
        }.onFailure { error ->
            reduce {
                state.copy(
                    isLoading = false,
                )
            }
            postSideEffect(
                LoginSideEffect.ShowError(
                    message = error.message
                        ?: "로그인 실패",
                ),
            )
        }
    }
}
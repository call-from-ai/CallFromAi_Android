package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.repository.LoginRepository
import kr.co.call.impl.viewmodel.state.LoginState
import org.orbitmvi.orbit.ContainerHost
import javax.inject.Inject
import org.orbitmvi.orbit.viewmodel.container

/**
 * 카카오 로그인 이후 서버 로그인 요청과 UI 상태를 관리하는 ViewModel
 * 로그인 성공 시 약관 화면 이동, 실패 시 오류 전달 SideEffect를 발생시킨다.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
): ViewModel(),
    ContainerHost<LoginState, LoginSideEffect> {
    override val container =
        container<LoginState, LoginSideEffect>(
            initialState = LoginState(),
        )

    /**
     * 카카오 SDK에서 받은 Access Token을 서버에 전달한다.
     * 서버 로그인 결과에 따라 로딩 상태와 화면 이동 또는 오류를 처리한다.
     */
    fun loginWithKakao(
        kakaoAccessToken: String,
    ) = intent {
        // 로그인 요청이 시작됐음을 UI 상태에 반영
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
            // 서버 로그인과 토큰 저장이 완료된 상태
            reduce {
                state.copy(
                    isLoading = false,
                )
            }

            // 로그인 성공 후 약관 화면으로 이동하도록 Entry에 전달
            postSideEffect(
                LoginSideEffect.NavigateToNext,
            )
        }.onFailure { error ->
            // 로그인 요청 실패 시 로딩 상태를 종료
            reduce {
                state.copy(
                    isLoading = false,
                )
            }

            // 서버 또는 카카오 로그인 오류 메시지를 화면에 전달
            postSideEffect(
                LoginSideEffect.ShowError(
                    message = error.message
                        ?: "로그인 실패",
                ),
            )
        }
    }
}
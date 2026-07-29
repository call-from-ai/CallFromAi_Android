package kr.co.call.impl.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.call.domain.repository.LoginRepository
import kr.co.call.domain.util.LoadStatus
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
    ContainerHost<LoadStatus, LoginSideEffect> {
    override val container =
        container<LoadStatus, LoginSideEffect>(
            initialState = LoadStatus.Idle,
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
            LoadStatus.Loading
        }

            loginRepository.loginWithKakao(
                kakaoAccessToken = kakaoAccessToken,
            )
        .onSuccess {
            //로그인 요청 성공
            reduce {
                LoadStatus.Idle
            }

            postSideEffect(
                LoginSideEffect.NavigateToNext,
            )
        }.onFailure { error ->
            val message=error.message ?:"로그인에 실패했습니다."
            reduce { LoadStatus.Error(message)}

            // 서버 또는 카카오 로그인 오류 메시지를 화면에 전달
            postSideEffect(
                LoginSideEffect.ShowError(message),
            )
        }
    }
}
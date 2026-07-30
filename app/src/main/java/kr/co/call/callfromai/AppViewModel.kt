package kr.co.call.callfromai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.co.call.datastore.TokenDataStore
import javax.inject.Inject

/**
 * 앱 시작 시 저장된 Access Token을 확인해 인증 상태를 결정한다.
 * Loading 동안 스플래시를 노출하고, 확인 완료 후 Authenticated/Unauthenticated로 전환한다.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
) : ViewModel() {

    private val _authState = MutableStateFlow<AppAuthState>(AppAuthState.Loading)
    val authState: StateFlow<AppAuthState> = _authState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val accessToken = tokenDataStore.getAccessToken()
            val elapsed = System.currentTimeMillis() - startTime
            val remaining = SPLASH_DURATION_MILLIS - elapsed
            if (remaining > 0L) delay(remaining)

            _authState.value = if (accessToken.isNullOrBlank()) {
                AppAuthState.Unauthenticated
            } else {
                AppAuthState.Authenticated
            }
        }
    }

    companion object {
        private const val SPLASH_DURATION_MILLIS = 3_000L
    }
}

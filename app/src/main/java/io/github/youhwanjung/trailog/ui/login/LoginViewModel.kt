package io.github.youhwanjung.trailog.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.youhwanjung.trailog.domain.repository.AuthRepository
import io.github.youhwanjung.trailog.domain.repository.InvalidCredentialsException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 로그인 화면이 그리는 데 필요한 모든 것. 화면 상태를 한 덩어리로 모아둔다.
 *
 * 필드를 StateFlow 여러 개로 쪼개지 않는 이유: 두 값이 따로 도착하면
 * "로딩 중인데 에러도 떠 있는" 같은 있을 수 없는 조합이 한 프레임 보일 수 있다.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
) {
    /** 파생 값은 저장하지 않고 계산한다. 저장하면 갱신을 빠뜨릴 수 있다. */
    val canSubmit: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && !isSubmitting
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    // 쓰기 가능한 쪽은 private, 밖에는 읽기 전용만 노출한다.
    // 화면이 상태를 직접 바꾸지 못하게 막는 것이 단방향 데이터 흐름의 핵심이다.
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun login() {
        val current = _uiState.value
        if (!current.canSubmit) return

        // viewModelScope 에서 돌리므로 ViewModel 이 정리될 때 이 코루틴도 함께 취소된다.
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val result = authRepository.login(current.email.trim(), current.password)

            // 성공했을 때 여기서 화면 전환을 하지 않는 점에 주목.
            // AuthRepository.isLoggedIn 이 true 로 바뀌고, 그걸 구독하는 AppViewModel 이
            // 알아서 메인 화면으로 갈아끼운다. 로그인 성공 경로가 한 곳뿐이라
            // 자동 로그인이나 토큰 만료도 같은 길로 처리된다.
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    errorMessage = result.exceptionOrNull()?.toMessage(),
                )
            }
        }
    }

    // TODO: 문자열은 strings.xml 로 옮기고 여기서는 에러 타입만 넘기는 편이 낫다.
    private fun Throwable.toMessage(): String = when (this) {
        is InvalidCredentialsException -> "이메일 또는 비밀번호를 확인해 주세요."
        else -> "로그인에 실패했습니다. 잠시 후 다시 시도해 주세요."
    }
}

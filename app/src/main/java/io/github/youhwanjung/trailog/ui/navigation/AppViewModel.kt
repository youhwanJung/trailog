package io.github.youhwanjung.trailog.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.youhwanjung.trailog.domain.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 *  앱 전체 차원의 상태가 더 필요해지면 여기에 추가한다.
 */
sealed interface AppUiState {
    data object Loading : AppUiState
    data object LoggedOut : AppUiState
    data object LoggedIn : AppUiState
}

/**
 * 나는 @HiltViewModel 이다.
 * 나는 AuthRepository가 하나 필요하다.
 * Hilt는 이걸 보고 넣어준다.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    authRepository: AuthRepository,
) : ViewModel() {

    val uiState: StateFlow<AppUiState> = authRepository.isLoggedIn
        .map { loggedIn -> if (loggedIn) AppUiState.LoggedIn else AppUiState.LoggedOut }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = AppUiState.Loading,
        )

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}


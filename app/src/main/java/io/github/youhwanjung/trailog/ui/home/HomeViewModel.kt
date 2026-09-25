package io.github.youhwanjung.trailog.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.youhwanjung.trailog.domain.model.Trip
import io.github.youhwanjung.trailog.domain.repository.AuthRepository
import io.github.youhwanjung.trailog.domain.repository.TripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    tripRepository: TripRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    // 목록은 저장소가 흘려보내는 대로 따라간다.
    // 기록이 추가되면 이 화면이 별도 새로고침 없이 갱신된다.
    val uiState: StateFlow<HomeUiState> = tripRepository.observeTrips()
        .map { trips -> HomeUiState(trips = trips, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = HomeUiState(),
        )

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

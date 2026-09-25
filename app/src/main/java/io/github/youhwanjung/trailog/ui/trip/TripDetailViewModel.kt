package io.github.youhwanjung.trailog.ui.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.youhwanjung.trailog.domain.model.Trip
import io.github.youhwanjung.trailog.domain.repository.TripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class TripDetailUiState(
    val trip: Trip? = null,
    val isLoading: Boolean = true,
)

/**
 * tripId 는 Hilt 가 모르는 값이다. 실행 중에 네비게이션에서 정해지기 때문이다.
 *
 * 그래서 assisted injection 을 쓴다.
 * - TripRepository 는 Hilt 가 그래프에서 꺼내오고
 * - tripId(@Assisted)는 화면이 호출 시점에 넘긴다
 * 둘을 Factory 가 합쳐서 ViewModel 을 만든다.
 *
 * 참고로 이 ViewModel 은 Route.TripDetail 을 모른다. Long 하나만 받는다.
 * 네비게이션 키를 그대로 받으면 ViewModel 이 네비게이션에 묶여서
 * 다른 진입 경로(딥링크, 위젯 등)에서 재사용하기 어려워진다.
 */
@HiltViewModel(assistedFactory = TripDetailViewModel.Factory::class)
class TripDetailViewModel @AssistedInject constructor(
    @Assisted private val tripId: Long,
    tripRepository: TripRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(tripId: Long): TripDetailViewModel
    }

    val uiState: StateFlow<TripDetailUiState> = tripRepository.observeTrip(tripId)
        .map { trip -> TripDetailUiState(trip = trip, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = TripDetailUiState(),
        )

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

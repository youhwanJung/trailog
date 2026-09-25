package io.github.youhwanjung.trailog.data.trip

import io.github.youhwanjung.trailog.domain.model.Trip
import io.github.youhwanjung.trailog.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 메모리 기반 임시 기록 저장소. 앱을 끄면 사라진다.
 *
 * TODO: Room 으로 교체한다. 인터페이스가 이미 Flow 를 돌려주고 있어서
 *       DAO 의 Flow 를 그대로 흘려보내면 화면 쪽은 바뀔 게 없다.
 */
@Singleton
class FakeTripRepository @Inject constructor() : TripRepository {

    private val trips = MutableStateFlow(SAMPLE_TRIPS)

    override fun observeTrips(): Flow<List<Trip>> = trips.asStateFlow()

    override fun observeTrip(id: Long): Flow<Trip?> =
        trips.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun addTrip(trip: Trip) {
        trips.value = trips.value + trip
    }

    private companion object {
        val SAMPLE_TRIPS = listOf(
            Trip(
                id = 1L,
                title = "제주 한달살기",
                place = "제주특별자치도",
                startDate = LocalDate.of(2026, 4, 1),
                endDate = LocalDate.of(2026, 4, 30),
                memo = "우도에서 본 일출",
            ),
            Trip(
                id = 2L,
                title = "교토 단풍",
                place = "교토, 일본",
                startDate = LocalDate.of(2025, 11, 14),
                endDate = LocalDate.of(2025, 11, 18),
                memo = "기요미즈데라 야간 개장",
            ),
            Trip(
                id = 3L,
                title = "강원도 서핑",
                place = "양양",
                startDate = LocalDate.of(2025, 8, 2),
                endDate = LocalDate.of(2025, 8, 4),
            ),
        )
    }
}

package io.github.youhwanjung.trailog.domain.repository

import io.github.youhwanjung.trailog.domain.model.Trip
import kotlinx.coroutines.flow.Flow

/**
 * 여행 기록 저장소.
 *
 * 조회는 Flow 로 노출한다. 한 번 읽고 끝나는 suspend fun 과 달리
 * 기록이 추가/수정되면 목록 화면과 상세 화면이 알아서 같이 갱신된다.
 */
interface TripRepository {

    fun observeTrips(): Flow<List<Trip>>

    /** 해당 id 의 기록. 없으면 null 을 흘린다(삭제된 경우 포함). */
    fun observeTrip(id: Long): Flow<Trip?>

    suspend fun addTrip(trip: Trip)
}

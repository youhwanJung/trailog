package io.github.youhwanjung.trailog.domain.model

import java.time.LocalDate

/**
 * 여행 기록 한 건.
 *
 * 도메인 모델에는 android.* 임포트가 없어야 한다.
 * 그래야 순수 JVM 단위 테스트에서 에뮬레이터 없이 그대로 쓸 수 있고,
 * 나중에 Room 엔티티나 서버 DTO 가 생겨도 이 모델은 영향을 받지 않는다.
 */
data class Trip(
    val id: Long,
    val title: String,
    val place: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val memo: String = "",
)

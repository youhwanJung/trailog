package io.github.youhwanjung.trailog.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 앱의 모든 목적지.
 *
 * 문자열 경로가 아니라 타입이라서 인자 누락/오타가 컴파일 시점에 잡힌다.
 * sealed 계층이면 kotlinx.serialization 이 다형성 직렬화를 알아서 처리하므로
 * 백스택을 프로세스 종료 후에도 복원할 수 있다.
 */
@Serializable
sealed interface Route : NavKey {

    /** 로그인 후 진입하는 첫 화면 */
    @Serializable
    data object Home : Route

    /** 기록 상세. 인자는 키의 프로퍼티로 넘긴다. */
    @Serializable
    data class TripDetail(val tripId: Long) : Route
}

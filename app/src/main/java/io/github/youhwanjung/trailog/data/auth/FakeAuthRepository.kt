package io.github.youhwanjung.trailog.data.auth

import io.github.youhwanjung.trailog.di.IoDispatcher
import io.github.youhwanjung.trailog.domain.repository.AuthRepository
import io.github.youhwanjung.trailog.domain.repository.InvalidCredentialsException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 메모리 기반 임시 인증 저장소. 서버가 붙기 전까지 쓴다.
 *
 * @Singleton 이라 앱 전체에서 인스턴스가 하나다.
 * 로그인 상태가 여러 개로 갈라지면 안 되므로 여기서는 필수다.
 *
 * TODO: 실제 인증 API + DataStore 기반 토큰 저장으로 교체한다.
 *       그때 바꾸는 건 이 클래스와 DataModule 의 바인딩 한 줄뿐이고,
 *       ViewModel 과 화면은 손대지 않는다.
 */
@Singleton
class FakeAuthRepository @Inject constructor(
    // @param: 을 명시하는 이유는 Kotlin 이 이 어노테이션을 프로퍼티에도 붙이도록 바뀔 예정이기 때문이다.
    // Dagger 는 생성자 파라미터에 붙은 한정자를 읽으므로 대상을 파라미터로 고정한다.
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: Flow<Boolean> = _isLoggedIn.asStateFlow()

    override suspend fun login(email: String, password: String): Result<Unit> =
        withContext(ioDispatcher) {
            delay(SIMULATED_NETWORK_DELAY_MS) // 로딩 상태를 눈으로 확인하려고 넣어둔 지연

            // 아무 이메일이나 통과시키되, 비밀번호 4자 미만이면 실패로 다룬다.
            if (password.length < MIN_PASSWORD_LENGTH) {
                return@withContext Result.failure(InvalidCredentialsException())
            }

            _isLoggedIn.value = true
            Result.success(Unit)
        }

    override suspend fun logout() {
        _isLoggedIn.value = false
    }

    private companion object {
        const val SIMULATED_NETWORK_DELAY_MS = 600L
        const val MIN_PASSWORD_LENGTH = 4
    }
}

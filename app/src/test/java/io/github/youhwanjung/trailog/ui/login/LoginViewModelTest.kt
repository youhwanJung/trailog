package io.github.youhwanjung.trailog.ui.login

import io.github.youhwanjung.trailog.domain.repository.AuthRepository
import io.github.youhwanjung.trailog.domain.repository.InvalidCredentialsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * ViewModel 로 분리했을 때 생기는 실익을 보여주는 테스트.
 *
 * 에뮬레이터도, Compose 도, Hilt 도 없이 순수 JVM 에서 돌아간다.
 * AuthRepository 가 인터페이스라서 가짜 구현을 그냥 생성자로 넣어주면 끝이기 때문이다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    /** viewModelScope 는 Dispatchers.Main 을 쓰므로 테스트용으로 갈아끼운다. */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `이메일과 비밀번호가 모두 있어야 제출할 수 있다`() {
        val viewModel = LoginViewModel(FakeAuth())

        assertFalse(viewModel.uiState.value.canSubmit)

        viewModel.onEmailChange("trailog@example.com")
        assertFalse(viewModel.uiState.value.canSubmit)

        viewModel.onPasswordChange("1234")
        assertTrue(viewModel.uiState.value.canSubmit)
    }

    @Test
    fun `로그인에 성공하면 인증 상태가 바뀌고 에러가 없다`() = runTest(testDispatcher) {
        val auth = FakeAuth()
        val viewModel = LoginViewModel(auth)

        viewModel.onEmailChange("trailog@example.com")
        viewModel.onPasswordChange("1234")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(auth.loggedIn.value)
        assertNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `로그인에 실패하면 에러 메시지가 남고 로그인 상태는 그대로다`() = runTest(testDispatcher) {
        val auth = FakeAuth(shouldSucceed = false)
        val viewModel = LoginViewModel(auth)

        viewModel.onEmailChange("trailog@example.com")
        viewModel.onPasswordChange("1234")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(auth.loggedIn.value)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `입력을 수정하면 이전 에러 메시지가 사라진다`() = runTest(testDispatcher) {
        val viewModel = LoginViewModel(FakeAuth(shouldSucceed = false))

        viewModel.onEmailChange("trailog@example.com")
        viewModel.onPasswordChange("1234")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.onPasswordChange("12345")

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `이메일 앞뒤 공백은 저장소에 넘기기 전에 제거된다`() = runTest(testDispatcher) {
        val auth = FakeAuth()
        val viewModel = LoginViewModel(auth)

        viewModel.onEmailChange("  trailog@example.com  ")
        viewModel.onPasswordChange("1234")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("trailog@example.com", auth.lastEmail)
    }

    private class FakeAuth(private val shouldSucceed: Boolean = true) : AuthRepository {
        val loggedIn = MutableStateFlow(false)
        var lastEmail: String? = null
            private set

        override val isLoggedIn: Flow<Boolean> = loggedIn

        override suspend fun login(email: String, password: String): Result<Unit> {
            lastEmail = email
            return if (shouldSucceed) {
                loggedIn.value = true
                Result.success(Unit)
            } else {
                Result.failure(InvalidCredentialsException())
            }
        }

        override suspend fun logout() {
            loggedIn.value = false
        }
    }
}

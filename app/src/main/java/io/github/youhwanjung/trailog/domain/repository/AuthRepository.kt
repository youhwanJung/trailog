package io.github.youhwanjung.trailog.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * 인증 상태의 단일 진실 공급원(single source of truth).
 *
 * 로그인 여부를 화면이 들고 있지 않고 여기서 흘려보낸다는 점이 중요하다.
 * LoginViewModel 은 login() 만 호출하고, 화면 전환은
 * isLoggedIn 을 구독하는 쪽(AppViewModel)이 알아서 반응한다.
 *
 * 인터페이스는 domain 에 두고 구현은 data 에 둔다.
 * 그래야 UI 가 "가짜 저장소냐 서버냐"를 모른 채로 돌아간다.
 */
interface AuthRepository {

    /** 현재 로그인 상태. 앱이 사는 동안 계속 흐른다. */
    val isLoggedIn: Flow<Boolean>

    /**
     * 로그인 시도. 성공하면 [isLoggedIn] 이 true 를 내보낸다.
     *
     * 실패를 예외로 던지지 않고 Result 로 돌려주는 이유는
     * 호출부(ViewModel)가 화면에 보여줄 에러로 다루도록 강제하기 위해서다.
     */
    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun logout()
}

/** 자격 증명이 틀렸을 때. 네트워크 오류 같은 다른 실패와 구분하려고 따로 둔다. */
class InvalidCredentialsException : Exception("invalid credentials")

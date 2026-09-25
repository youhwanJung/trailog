package io.github.youhwanjung.trailog

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Hilt 의존성 그래프의 뿌리.
 *
 * @HiltAndroidApp 이 붙으면 컴파일 시점에 Hilt_TrailogApplication 이 생성되고,
 * 앱 전체 수명을 갖는 SingletonComponent 가 여기서 만들어진다.
 * AndroidManifest 의 android:name 이 이 클래스를 가리켜야 동작한다.
 */
@HiltAndroidApp
class TrailogApplication : Application()

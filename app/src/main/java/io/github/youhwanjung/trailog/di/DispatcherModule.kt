package io.github.youhwanjung.trailog.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/**
 * Dispatchers.IO 를 코드 안에서 직접 부르지 않고 주입받기 위한 한정자.
 *
 * CoroutineDispatcher 타입만으로는 Default 인지 IO 인지 Dagger 가 구분하지 못하므로
 * 한정자를 붙여 구분한다. 테스트에서는 이 자리에 TestDispatcher 를 갈아끼울 수 있다.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

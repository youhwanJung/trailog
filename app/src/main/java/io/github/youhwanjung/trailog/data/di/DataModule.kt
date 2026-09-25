package io.github.youhwanjung.trailog.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.youhwanjung.trailog.data.auth.FakeAuthRepository
import io.github.youhwanjung.trailog.data.trip.FakeTripRepository
import io.github.youhwanjung.trailog.domain.repository.AuthRepository
import io.github.youhwanjung.trailog.domain.repository.TripRepository
import javax.inject.Singleton

/**
 * 인터페이스와 구현을 연결한다.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FakeAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTripRepository(impl: FakeTripRepository): TripRepository
}

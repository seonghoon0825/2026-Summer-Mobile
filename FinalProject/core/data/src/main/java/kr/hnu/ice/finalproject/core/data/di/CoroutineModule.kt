package kr.hnu.ice.finalproject.core.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.hnu.ice.finalproject.core.common.coroutine.DefaultDispatcherProvider
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import javax.inject.Singleton

/** 코루틴 Dispatcher 제공. core:common의 기본 구현을 Hilt로 주입 가능하게 한다. */
@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
}
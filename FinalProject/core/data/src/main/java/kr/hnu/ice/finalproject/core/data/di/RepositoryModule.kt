package kr.hnu.ice.finalproject.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.hnu.ice.finalproject.core.data.remote.AndroidAssetReader
import kr.hnu.ice.finalproject.core.data.remote.AssetReader
import kr.hnu.ice.finalproject.core.data.repository.CartRepository
import kr.hnu.ice.finalproject.core.data.repository.CartRepositoryImpl
import kr.hnu.ice.finalproject.core.data.repository.CompareRepository
import kr.hnu.ice.finalproject.core.data.repository.CompareRepositoryImpl
import kr.hnu.ice.finalproject.core.data.repository.OrderDraftRepository
import kr.hnu.ice.finalproject.core.data.repository.OrderDraftRepositoryImpl
import kr.hnu.ice.finalproject.core.data.repository.OrderRepository
import kr.hnu.ice.finalproject.core.data.repository.OrderRepositoryImpl
import kr.hnu.ice.finalproject.core.data.repository.ProductRepository
import kr.hnu.ice.finalproject.core.data.repository.ProductRepositoryImpl
import kr.hnu.ice.finalproject.core.data.repository.ReviewRepository
import kr.hnu.ice.finalproject.core.data.repository.ReviewRepositoryImpl
import kr.hnu.ice.finalproject.core.data.repository.SaleRepository
import kr.hnu.ice.finalproject.core.data.repository.SaleRepositoryImpl
import kr.hnu.ice.finalproject.core.data.repository.SearchHistoryRepository
import kr.hnu.ice.finalproject.core.data.repository.SearchHistoryRepositoryImpl
import kr.hnu.ice.finalproject.core.data.repository.UserPreferencesRepository
import kr.hnu.ice.finalproject.core.data.repository.UserPreferencesRepositoryImpl
import kr.hnu.ice.finalproject.core.data.repository.WishRepository
import kr.hnu.ice.finalproject.core.data.repository.WishRepositoryImpl
import javax.inject.Singleton

/**
 * 데이터 출처를 숨기는 핵심: 상위 계층은 인터페이스만 주입받고,
 * 여기서 실제 구현체(Impl)를 바인딩한다.
 * 나중에 서버 구현으로 바꾸려면 이 바인딩만 교체하면 된다.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAssetReader(impl: AndroidAssetReader): AssetReader

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindWishRepository(impl: WishRepositoryImpl): WishRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @Binds
    @Singleton
    abstract fun bindOrderDraftRepository(impl: OrderDraftRepositoryImpl): OrderDraftRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindSaleRepository(impl: SaleRepositoryImpl): SaleRepository

    @Binds
    @Singleton
    abstract fun bindCompareRepository(impl: CompareRepositoryImpl): CompareRepository

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(impl: SearchHistoryRepositoryImpl): SearchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}
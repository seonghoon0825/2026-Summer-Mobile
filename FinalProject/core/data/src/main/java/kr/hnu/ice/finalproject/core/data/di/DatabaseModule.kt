package kr.hnu.ice.finalproject.core.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.hnu.ice.finalproject.core.data.local.AppDatabase
import kr.hnu.ice.finalproject.core.data.local.dao.CartDao
import kr.hnu.ice.finalproject.core.data.local.dao.OrderDao
import kr.hnu.ice.finalproject.core.data.local.dao.RecentProductDao
import kr.hnu.ice.finalproject.core.data.local.dao.RecentSearchDao
import kr.hnu.ice.finalproject.core.data.local.dao.WishDao
import javax.inject.Singleton

/** Room DB와 각 DAO를 제공한다. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideCartDao(db: AppDatabase): CartDao = db.cartDao()

    @Provides
    fun provideWishDao(db: AppDatabase): WishDao = db.wishDao()

    @Provides
    fun provideRecentProductDao(db: AppDatabase): RecentProductDao = db.recentProductDao()

    @Provides
    fun provideRecentSearchDao(db: AppDatabase): RecentSearchDao = db.recentSearchDao()

    @Provides
    fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()
}
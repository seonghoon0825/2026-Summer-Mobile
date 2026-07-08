package kr.hnu.ice.finalproject.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import kr.hnu.ice.finalproject.core.data.local.dao.CartDao
import kr.hnu.ice.finalproject.core.data.local.dao.OrderDao
import kr.hnu.ice.finalproject.core.data.local.dao.RecentProductDao
import kr.hnu.ice.finalproject.core.data.local.dao.RecentSearchDao
import kr.hnu.ice.finalproject.core.data.local.dao.WishDao
import kr.hnu.ice.finalproject.core.data.local.entity.CartItemEntity
import kr.hnu.ice.finalproject.core.data.local.entity.OrderEntity
import kr.hnu.ice.finalproject.core.data.local.entity.RecentProductEntity
import kr.hnu.ice.finalproject.core.data.local.entity.RecentSearchEntity
import kr.hnu.ice.finalproject.core.data.local.entity.WishItemEntity

/**
 * 앱 로컬 DB. 장바구니/찜/최근본상품/최근검색어/주문을 저장한다.
 * (상품/카테고리/리뷰 같은 카탈로그 데이터는 assets JSON에서 읽으므로 DB에 두지 않는다)
 */
@Database(
    entities = [
        CartItemEntity::class,
        WishItemEntity::class,
        RecentProductEntity::class,
        RecentSearchEntity::class,
        OrderEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun wishDao(): WishDao
    abstract fun recentProductDao(): RecentProductDao
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun orderDao(): OrderDao

    companion object {
        const val DATABASE_NAME = "final_app.db"
    }
}
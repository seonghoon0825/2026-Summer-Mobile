package kr.hnu.ice.finalproject.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kr.hnu.ice.finalproject.core.data.local.entity.RecentProductEntity

@Dao
interface RecentProductDao {

    @Query("SELECT * FROM recent_products ORDER BY viewedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<RecentProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: RecentProductEntity)

    /**
     * 최신순 [limit]개만 남기고 그보다 오래된 기록을 삭제한다.
     * (upsert 직후 호출해 최근 본 상품이 [limit]개를 넘지 않도록 유지)
     */
    @Query(
        "DELETE FROM recent_products WHERE productId NOT IN (" +
            "SELECT productId FROM recent_products ORDER BY viewedAt DESC LIMIT :limit)",
    )
    suspend fun trimToLimit(limit: Int)

    @Query("DELETE FROM recent_products WHERE productId = :productId")
    suspend fun delete(productId: String)

    @Query("DELETE FROM recent_products")
    suspend fun clear()
}
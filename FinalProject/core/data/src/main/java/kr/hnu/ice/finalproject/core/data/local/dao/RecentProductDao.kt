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

    @Query("DELETE FROM recent_products WHERE productId = :productId")
    suspend fun delete(productId: String)

    @Query("DELETE FROM recent_products")
    suspend fun clear()
}
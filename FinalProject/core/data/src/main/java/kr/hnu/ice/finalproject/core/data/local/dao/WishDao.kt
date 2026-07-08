package kr.hnu.ice.finalproject.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kr.hnu.ice.finalproject.core.data.local.entity.WishItemEntity

@Dao
interface WishDao {

    @Query("SELECT * FROM wish_items ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<WishItemEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM wish_items WHERE productId = :productId)")
    fun observeIsWished(productId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WishItemEntity)

    @Query("DELETE FROM wish_items WHERE productId = :productId")
    suspend fun delete(productId: String)
}
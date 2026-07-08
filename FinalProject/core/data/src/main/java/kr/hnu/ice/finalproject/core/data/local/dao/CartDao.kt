package kr.hnu.ice.finalproject.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kr.hnu.ice.finalproject.core.data.local.entity.CartItemEntity

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<CartItemEntity>>

    /** 같은 옵션이 이미 있으면 덮어쓴다(REPLACE). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId AND color = :color AND size = :size")
    suspend fun updateQuantity(productId: String, color: String, size: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId AND color = :color AND size = :size")
    suspend fun delete(productId: String, color: String, size: String)

    @Query("DELETE FROM cart_items")
    suspend fun clear()
}
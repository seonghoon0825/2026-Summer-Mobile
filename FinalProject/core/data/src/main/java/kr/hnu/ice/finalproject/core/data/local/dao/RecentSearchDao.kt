package kr.hnu.ice.finalproject.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kr.hnu.ice.finalproject.core.data.local.entity.RecentSearchEntity

@Dao
interface RecentSearchDao {

    @Query("SELECT * FROM recent_searches ORDER BY searchedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE keyword = :keyword")
    suspend fun delete(keyword: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clear()
}
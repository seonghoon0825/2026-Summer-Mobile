package kr.hnu.ice.finalproject.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import kr.hnu.ice.finalproject.core.data.local.dao.RecentSearchDao
import kr.hnu.ice.finalproject.core.data.local.entity.RecentSearchEntity
import javax.inject.Inject
import javax.inject.Singleton

/** 최근 검색어 저장소. (Room) */
interface SearchHistoryRepository {
    fun getRecentSearches(): Flow<List<String>>
    suspend fun addSearch(keyword: String)
    suspend fun removeSearch(keyword: String)
    suspend fun clearSearches()
}

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val recentSearchDao: RecentSearchDao,
    private val dispatchers: DispatcherProvider,
) : SearchHistoryRepository {

    override fun getRecentSearches(): Flow<List<String>> =
        recentSearchDao.observeRecent(RECENT_LIMIT)
            .map { entities -> entities.map { it.keyword } }
            .flowOn(dispatchers.io)

    override suspend fun addSearch(keyword: String) {
        val trimmed = keyword.trim()
        if (trimmed.isBlank()) return
        recentSearchDao.upsert(
            RecentSearchEntity(keyword = trimmed, searchedAt = System.currentTimeMillis()),
        )
    }

    override suspend fun removeSearch(keyword: String) {
        recentSearchDao.delete(keyword)
    }

    override suspend fun clearSearches() {
        recentSearchDao.clear()
    }

    private companion object {
        const val RECENT_LIMIT = 10
    }
}

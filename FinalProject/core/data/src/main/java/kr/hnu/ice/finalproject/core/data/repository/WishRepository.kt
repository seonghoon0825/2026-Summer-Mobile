package kr.hnu.ice.finalproject.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import kr.hnu.ice.finalproject.core.data.local.dao.WishDao
import kr.hnu.ice.finalproject.core.data.local.entity.WishItemEntity
import kr.hnu.ice.finalproject.core.model.WishItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 찜(위시리스트) 저장소. Room에 productId만 저장하고,
 * 조회 시 ProductRepository로 상품을 복원해 WishItem으로 노출한다.
 */
interface WishRepository {
    fun getWishItems(): Flow<List<WishItem>>
    fun isWished(productId: String): Flow<Boolean>
    suspend fun addWish(productId: String)
    suspend fun removeWish(productId: String)

    /** 찜 상태 토글(있으면 제거, 없으면 추가). */
    suspend fun toggleWish(productId: String)
}

@Singleton
class WishRepositoryImpl @Inject constructor(
    private val wishDao: WishDao,
    private val productRepository: ProductRepository,
    private val dispatchers: DispatcherProvider,
) : WishRepository {

    override fun getWishItems(): Flow<List<WishItem>> =
        wishDao.observeAll()
            .map { entities ->
                entities.mapNotNull { entity ->
                    productRepository.getProductById(entity.productId)?.let { WishItem(it) }
                }
            }
            .flowOn(dispatchers.io)

    override fun isWished(productId: String): Flow<Boolean> =
        wishDao.observeIsWished(productId).flowOn(dispatchers.io)

    override suspend fun addWish(productId: String) {
        wishDao.insert(WishItemEntity(productId = productId, addedAt = System.currentTimeMillis()))
    }

    override suspend fun removeWish(productId: String) {
        wishDao.delete(productId)
    }

    override suspend fun toggleWish(productId: String) {
        val wished = wishDao.observeIsWished(productId).first()
        if (wished) removeWish(productId) else addWish(productId)
    }
}
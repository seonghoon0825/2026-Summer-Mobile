package kr.hnu.ice.finalproject.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import kr.hnu.ice.finalproject.core.data.local.dao.WishDao
import kr.hnu.ice.finalproject.core.data.local.entity.WishItemEntity
import kr.hnu.ice.finalproject.core.model.SaleWishItem
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

    /**
     * 찜한 상품 중 현재 세일 중인 것만 반환한다(최신순).
     * 세일 여부는 두 경로로 판단한다:
     *  - 런타임 Mock 세일([SaleRepository]) — 데모 트리거로 내려간 가격
     *  - 상품 자체의 세일([Product.isOnSale]) — Mock 데이터(JSON)에 세일이 걸린 상품
     */
    fun getOnSaleWishItems(): Flow<List<SaleWishItem>>
}

@Singleton
class WishRepositoryImpl @Inject constructor(
    private val wishDao: WishDao,
    private val productRepository: ProductRepository,
    private val saleRepository: SaleRepository,
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

    override fun getOnSaleWishItems(): Flow<List<SaleWishItem>> =
        combine(getWishItems(), saleRepository.observeSalePrices()) { items, salePrices ->
            items.mapNotNull { item ->
                val product = item.product
                val runtimeSale = salePrices[product.id]
                when {
                    // 런타임 세일이 현재가보다 낮으면 그 세일가를 적용
                    runtimeSale != null && runtimeSale < product.price ->
                        SaleWishItem(product, originalPrice = product.price, salePrice = runtimeSale)
                    // 상품 자체가 세일 중(Mock 데이터에 세일이 걸린 경우)
                    product.isOnSale ->
                        SaleWishItem(product, originalPrice = product.originalPrice!!, salePrice = product.price)
                    else -> null
                }
            }
        }.flowOn(dispatchers.io)

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
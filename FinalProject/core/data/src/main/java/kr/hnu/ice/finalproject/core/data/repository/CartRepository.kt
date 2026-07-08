package kr.hnu.ice.finalproject.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import kr.hnu.ice.finalproject.core.data.local.dao.CartDao
import kr.hnu.ice.finalproject.core.data.local.entity.CartItemEntity
import kr.hnu.ice.finalproject.core.model.CartItem
import kr.hnu.ice.finalproject.core.model.Product
import kr.hnu.ice.finalproject.core.model.ProductOption
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 장바구니 저장소. Room에는 productId + 옵션 + 수량만 저장하고,
 * 조회 시 ProductRepository로 상품 정보를 복원해 CartItem으로 노출한다.
 */
interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>

    /** 장바구니에 담기(같은 옵션이면 수량을 [quantity]로 설정). */
    suspend fun addToCart(product: Product, option: ProductOption, quantity: Int)

    /** 특정 옵션의 수량 변경. */
    suspend fun updateQuantity(productId: String, option: ProductOption, quantity: Int)

    /** 특정 옵션 삭제. */
    suspend fun removeFromCart(productId: String, option: ProductOption)

    /** 장바구니 비우기. */
    suspend fun clearCart()
}

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val productRepository: ProductRepository,
    private val dispatchers: DispatcherProvider,
) : CartRepository {

    override fun getCartItems(): Flow<List<CartItem>> =
        cartDao.observeAll()
            .map { entities -> entities.mapNotNull { it.toCartItemOrNull() } }
            .flowOn(dispatchers.io)

    override suspend fun addToCart(product: Product, option: ProductOption, quantity: Int) {
        cartDao.upsert(
            CartItemEntity(
                productId = product.id,
                color = option.color,
                size = option.size,
                quantity = quantity,
                addedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun updateQuantity(productId: String, option: ProductOption, quantity: Int) {
        cartDao.updateQuantity(productId, option.color, option.size, quantity)
    }

    override suspend fun removeFromCart(productId: String, option: ProductOption) {
        cartDao.delete(productId, option.color, option.size)
    }

    override suspend fun clearCart() {
        cartDao.clear()
    }

    /** 엔티티 → 도메인 CartItem. 상품을 찾지 못하면 null(목록에서 제외). */
    private suspend fun CartItemEntity.toCartItemOrNull(): CartItem? {
        val product = productRepository.getProductById(productId) ?: return null
        // 저장된 옵션과 일치하는 상품 옵션을 찾고, 없으면 저장된 값으로 재구성(재고 0).
        val option = product.options.firstOrNull { it.color == color && it.size == size }
            ?: ProductOption(color = color, size = size, stock = 0)
        return CartItem(product = product, selectedOption = option, quantity = quantity)
    }
}
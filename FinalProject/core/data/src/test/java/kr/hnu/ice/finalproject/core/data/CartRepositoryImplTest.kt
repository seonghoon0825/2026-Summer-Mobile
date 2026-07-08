package kr.hnu.ice.finalproject.core.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import kr.hnu.ice.finalproject.core.data.local.dao.CartDao
import kr.hnu.ice.finalproject.core.data.local.entity.CartItemEntity
import kr.hnu.ice.finalproject.core.data.repository.CartRepositoryImpl
import kr.hnu.ice.finalproject.core.data.repository.ProductRepository
import kr.hnu.ice.finalproject.core.model.Category
import kr.hnu.ice.finalproject.core.model.Product
import kr.hnu.ice.finalproject.core.model.ProductOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * "옵션을 골라 담으면 장바구니에 나타난다"를 디바이스 없이 검증한다.
 * Room DAO와 ProductRepository를 in-memory fake로 대체해 CartRepositoryImpl의 왕복만 테스트한다.
 */
class CartRepositoryImplTest {

    private val sampleProduct = Product(
        id = "p1",
        name = "테스트 상품",
        brand = "테스트 브랜드",
        price = 10_000,
        imageUrl = "https://example.com/p1.jpg",
        category = Category("top", "상의"),
        options = listOf(ProductOption("블랙", "M", 5)),
        rating = 4.0,
        reviewCount = 10,
        description = "설명",
    )

    // (productId+color+size)로 유일성을 보장하는 in-memory CartDao
    private class FakeCartDao : CartDao {
        private val state = MutableStateFlow<List<CartItemEntity>>(emptyList())
        override fun observeAll(): Flow<List<CartItemEntity>> = state
        override suspend fun upsert(item: CartItemEntity) {
            val key = Triple(item.productId, item.color, item.size)
            state.value = state.value.filterNot { Triple(it.productId, it.color, it.size) == key } + item
        }
        override suspend fun updateQuantity(productId: String, color: String, size: String, quantity: Int) {
            state.value = state.value.map {
                if (it.productId == productId && it.color == color && it.size == size) it.copy(quantity = quantity) else it
            }
        }
        override suspend fun delete(productId: String, color: String, size: String) {
            state.value = state.value.filterNot { it.productId == productId && it.color == color && it.size == size }
        }
        override suspend fun clear() { state.value = emptyList() }
    }

    // getProductById만 의미 있게 구현한 fake ProductRepository
    private inner class FakeProductRepository : ProductRepository {
        override fun getProducts(): Flow<List<Product>> = flowOf(listOf(sampleProduct))
        override suspend fun getProductById(id: String): Product? =
            if (id == sampleProduct.id) sampleProduct else null
        override fun getProductsByCategory(categoryId: String): Flow<List<Product>> = flowOf(emptyList())
        override fun searchProducts(query: String): Flow<List<Product>> = flowOf(emptyList())
        override fun getCategories(): Flow<List<Category>> = flowOf(emptyList())
        override suspend fun addRecentViewed(productId: String) = Unit
        override fun getRecentViewedProducts(): Flow<List<Product>> = flowOf(emptyList())
    }

    private class TestDispatcherProvider : DispatcherProvider {
        private val d: CoroutineDispatcher = Dispatchers.Unconfined
        override val main = d
        override val io = d
        override val default = d
    }

    private fun createRepository() = CartRepositoryImpl(
        cartDao = FakeCartDao(),
        productRepository = FakeProductRepository(),
        dispatchers = TestDispatcherProvider(),
    )

    @Test
    fun `옵션을 골라 담으면 장바구니에 그 상품이 나타난다`() = runTest {
        val repo = createRepository()
        val option = sampleProduct.options.first() // 블랙/M

        repo.addToCart(sampleProduct, option, quantity = 2)

        val cart = repo.getCartItems().first()
        assertEquals(1, cart.size)
        val item = cart.first()
        assertEquals("p1", item.product.id)
        assertEquals("블랙", item.selectedOption.color)
        assertEquals("M", item.selectedOption.size)
        assertEquals(2, item.quantity)
        // lineTotal = 단가 * 수량
        assertEquals(20_000, item.lineTotal)
    }

    @Test
    fun `수량 변경과 삭제가 반영된다`() = runTest {
        val repo = createRepository()
        val option = sampleProduct.options.first()
        repo.addToCart(sampleProduct, option, quantity = 1)

        repo.updateQuantity("p1", option, quantity = 3)
        assertEquals(3, repo.getCartItems().first().first().quantity)

        repo.removeFromCart("p1", option)
        assertTrue(repo.getCartItems().first().isEmpty())
    }
}
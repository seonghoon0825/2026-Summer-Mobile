package kr.hnu.ice.finalproject.core.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import kr.hnu.ice.finalproject.core.data.local.dao.OrderDao
import kr.hnu.ice.finalproject.core.data.local.entity.OrderEntity
import kr.hnu.ice.finalproject.core.data.repository.OrderRepositoryImpl
import kr.hnu.ice.finalproject.core.model.CartItem
import kr.hnu.ice.finalproject.core.model.Category
import kr.hnu.ice.finalproject.core.model.OrderStatus
import kr.hnu.ice.finalproject.core.model.Product
import kr.hnu.ice.finalproject.core.model.ProductOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * "주문 → 주문내역에 남는다"를 디바이스 없이 검증한다.
 * Room OrderDao를 in-memory fake로 대체해 OrderRepositoryImpl의 저장/조회 왕복을 테스트한다.
 */
class OrderRepositoryImplTest {

    private class FakeOrderDao : OrderDao {
        private val state = MutableStateFlow<List<OrderEntity>>(emptyList())
        override fun observeAll(): Flow<List<OrderEntity>> = state
        override suspend fun getById(id: String): OrderEntity? = state.value.firstOrNull { it.id == id }
        override suspend fun insert(order: OrderEntity) {
            state.value = state.value.filterNot { it.id == order.id } + order
        }
        override suspend fun updateStatus(id: String, status: String) {
            state.value = state.value.map { if (it.id == id) it.copy(status = status) else it }
        }
    }

    private class TestDispatcherProvider : DispatcherProvider {
        private val d: CoroutineDispatcher = Dispatchers.Unconfined
        override val main = d
        override val io = d
        override val default = d
    }

    private fun sampleCartItem(): CartItem {
        val product = Product(
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
        return CartItem(product, ProductOption("블랙", "M", 5), quantity = 2)
    }

    private fun createRepository() = OrderRepositoryImpl(
        orderDao = FakeOrderDao(),
        json = Json { ignoreUnknownKeys = true },
        dispatchers = TestDispatcherProvider(),
    )

    @Test
    fun `주문을 생성하면 주문내역에 남고 항목 스냅샷이 보존된다`() = runTest {
        val repo = createRepository()
        val items = listOf(sampleCartItem())

        val created = repo.createOrder(items = items, address = "서울시 강남구 (홍길동, 010-0000-0000)")

        // 생성된 주문 검증
        assertEquals(20_000, created.totalPrice) // 10,000 * 2
        assertEquals(OrderStatus.ORDERED, created.status)

        // 주문내역 조회에 반영되는지
        val orders = repo.getOrders().first()
        assertEquals(1, orders.size)
        val order = orders.first()
        assertEquals(created.id, order.id)
        assertEquals("서울시 강남구 (홍길동, 010-0000-0000)", order.address)

        // 항목 스냅샷(JSON 직렬화 → 역직렬화) 복원 검증
        assertEquals(1, order.items.size)
        val line = order.items.first()
        assertEquals("p1", line.product.id)
        assertEquals("테스트 브랜드", line.product.brand)
        assertEquals("블랙", line.selectedOption.color)
        assertEquals(2, line.quantity)

        // 단건 조회
        assertNotNull(repo.getOrderById(created.id))
    }
}
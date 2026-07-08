package kr.hnu.ice.finalproject.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.hnu.ice.finalproject.core.common.coroutine.DispatcherProvider
import kr.hnu.ice.finalproject.core.data.local.dao.OrderDao
import kr.hnu.ice.finalproject.core.data.local.entity.OrderEntity
import kr.hnu.ice.finalproject.core.model.CartItem
import kr.hnu.ice.finalproject.core.model.Category
import kr.hnu.ice.finalproject.core.model.Order
import kr.hnu.ice.finalproject.core.model.OrderStatus
import kr.hnu.ice.finalproject.core.model.Product
import kr.hnu.ice.finalproject.core.model.ProductOption
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 주문 저장소. 주문은 "구매 시점 스냅샷"이라 상품을 재조회하지 않고,
 * 주문 항목을 JSON으로 직렬화해 Room에 함께 저장한다.
 */
interface OrderRepository {
    fun getOrders(): Flow<List<Order>>
    suspend fun getOrderById(id: String): Order?

    /** 주문 생성. 장바구니 항목들을 스냅샷으로 저장하고 생성된 주문을 반환한다. */
    suspend fun createOrder(items: List<CartItem>, address: String): Order

    /**
     * Mock 배송 상태 전환: 주문 상태를 다음 단계로 한 칸 진행한다.
     * (ORDERED → PAID → SHIPPING → DELIVERED, DELIVERED면 변화 없음)
     */
    suspend fun advanceOrderStatus(orderId: String)
}

/** 주문 항목 스냅샷(직렬화용). 상품이 나중에 바뀌어도 주문 내역은 이 값으로 보존된다. */
@Serializable
internal data class OrderLineData(
    val productId: String,
    val name: String,
    val brand: String,
    val price: Int,
    val imageUrl: String,
    val categoryId: String,
    val categoryName: String,
    val color: String,
    val size: String,
    val quantity: Int,
)

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val json: Json,
    private val dispatchers: DispatcherProvider,
) : OrderRepository {

    override fun getOrders(): Flow<List<Order>> =
        orderDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatchers.io)

    override suspend fun getOrderById(id: String): Order? =
        orderDao.getById(id)?.toDomain()

    override suspend fun advanceOrderStatus(orderId: String) {
        val entity = orderDao.getById(orderId) ?: return
        val current = runCatching { OrderStatus.valueOf(entity.status) }
            .getOrDefault(OrderStatus.ORDERED)
        val next = OrderStatus.entries.getOrNull(current.ordinal + 1) ?: return // 이미 배송완료면 종료
        orderDao.updateStatus(orderId, next.name)
    }

    override suspend fun createOrder(items: List<CartItem>, address: String): Order {
        val now = System.currentTimeMillis()
        val id = "order_${UUID.randomUUID()}"
        val lines = items.map { it.toLineData() }
        val entity = OrderEntity(
            id = id,
            totalPrice = items.sumOf { it.lineTotal },
            status = OrderStatus.ORDERED.name,
            orderedAt = now,
            address = address,
            itemsJson = json.encodeToString(lines),
        )
        orderDao.insert(entity)
        return entity.toDomain()
    }

    // ---- 매핑 ----

    private fun CartItem.toLineData(): OrderLineData = OrderLineData(
        productId = product.id,
        name = product.name,
        brand = product.brand,
        price = product.price,
        imageUrl = product.imageUrl,
        categoryId = product.category.id,
        categoryName = product.category.name,
        color = selectedOption.color,
        size = selectedOption.size,
        quantity = quantity,
    )

    private fun OrderLineData.toCartItem(): CartItem {
        val product = Product(
            id = productId,
            name = name,
            brand = brand,
            price = price,
            imageUrl = imageUrl,
            category = Category(categoryId, categoryName),
            options = listOf(ProductOption(color, size, 0)),
            rating = 0.0,
            reviewCount = 0,
            description = "",
        )
        return CartItem(
            product = product,
            selectedOption = ProductOption(color, size, 0),
            quantity = quantity,
        )
    }

    private fun OrderEntity.toDomain(): Order {
        val lines = json.decodeFromString<List<OrderLineData>>(itemsJson)
        return Order(
            id = id,
            items = lines.map { it.toCartItem() },
            totalPrice = totalPrice,
            status = runCatching { OrderStatus.valueOf(status) }.getOrDefault(OrderStatus.ORDERED),
            orderedAt = orderedAt,
            address = address,
        )
    }
}
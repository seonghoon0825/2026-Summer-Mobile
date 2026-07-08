package kr.hnu.ice.finalproject.core.model

/**
 * 주문 도메인 모델.
 *
 * @param id 주문 식별자
 * @param items 주문에 포함된 항목들
 * @param totalPrice 총 결제 금액 (원, KRW 단위의 정수)
 * @param status 주문 상태
 * @param orderedAt 주문 시각 (epoch milliseconds — 프레임워크 의존 없이 Long으로 보관)
 * @param address 배송지 주소
 */
data class Order(
    val id: String,
    val items: List<CartItem>,
    val totalPrice: Int,
    val status: OrderStatus,
    val orderedAt: Long,
    val address: String,
)

/**
 * 주문 처리 상태.
 * ORDERED(주문완료) → PAID(결제완료) → SHIPPING(배송중) → DELIVERED(배송완료)
 */
enum class OrderStatus {
    ORDERED,
    PAID,
    SHIPPING,
    DELIVERED,
}
package kr.hnu.ice.finalproject.feature.cart

/** 쿠폰 종류: 정액 할인 / 정률 할인. */
enum class CouponType { FIXED, PERCENT }

/**
 * 쿠폰. (백엔드 없이 Mock)
 *
 * @param value FIXED면 할인액(원), PERCENT면 할인율(%)
 * @param minOrderAmount 최소 주문 금액(이상이어야 사용 가능)
 * @param maxDiscount 정률 쿠폰의 최대 할인 한도
 */
data class Coupon(
    val id: String,
    val name: String,
    val type: CouponType,
    val value: Int,
    val minOrderAmount: Int = 0,
    val maxDiscount: Int = Int.MAX_VALUE,
) {
    /** 해당 주문 금액에서 사용 가능한지. */
    fun isApplicable(subtotal: Int): Boolean = subtotal >= minOrderAmount

    /**
     * 주어진 상품 금액에 대한 실제 할인액.
     * 사용 불가면 0, 정률은 최대 한도와 주문 금액을 넘지 않도록 캡한다.
     */
    fun discountFor(subtotal: Int): Int {
        if (!isApplicable(subtotal)) return 0
        val raw = when (type) {
            CouponType.FIXED -> value
            CouponType.PERCENT -> subtotal * value / 100
        }
        return raw.coerceAtMost(maxDiscount).coerceAtMost(subtotal)
    }

    companion object {
        /** Mock 보유 쿠폰 목록. */
        val MOCK_COUPONS = listOf(
            Coupon("c1", "신규가입 3,000원 할인", CouponType.FIXED, value = 3_000),
            Coupon("c2", "10% 할인 (3만원 이상)", CouponType.PERCENT, value = 10, minOrderAmount = 30_000, maxDiscount = 20_000),
            Coupon("c3", "5,000원 할인 (5만원 이상)", CouponType.FIXED, value = 5_000, minOrderAmount = 50_000),
        )
    }
}

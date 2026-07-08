package kr.hnu.ice.finalproject.core.model

/**
 * 세일(가격 인하) 중인 찜 상품. 원가와 세일가를 함께 담아
 * "찜한 상품 중 지금 세일 중인 것"을 표현한다. (가격 인하 알림에 사용)
 *
 * @param product 찜한 상품
 * @param originalPrice 세일 전 원래 가격
 * @param salePrice 현재 세일가 (originalPrice보다 낮음)
 */
data class SaleWishItem(
    val product: Product,
    val originalPrice: Int,
    val salePrice: Int,
) {
    /** 할인율(%). 예) 59000 -> 41300 이면 30. */
    val discountRate: Int
        get() = if (originalPrice > 0) (originalPrice - salePrice) * 100 / originalPrice else 0
}
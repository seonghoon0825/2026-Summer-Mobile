package kr.hnu.ice.finalproject.core.model

/**
 * 장바구니 항목. 어떤 상품을 어떤 옵션으로 몇 개 담았는지 나타낸다.
 *
 * @param product 담은 상품
 * @param selectedOption 선택한 옵션(색상/사이즈)
 * @param quantity 수량
 */
data class CartItem(
    val product: Product,
    val selectedOption: ProductOption,
    val quantity: Int,
) {
    /** 이 항목의 합계 금액 (단가 × 수량). */
    val lineTotal: Int get() = product.price * quantity
}
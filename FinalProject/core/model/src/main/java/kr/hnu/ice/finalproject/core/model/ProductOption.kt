package kr.hnu.ice.finalproject.core.model

/**
 * 상품의 구매 옵션(색상 + 사이즈 조합)과 재고.
 *
 * @param color 색상 (예: "블랙")
 * @param size 사이즈 (예: "M", "270")
 * @param stock 해당 옵션의 남은 재고 수량
 */
data class ProductOption(
    val color: String,
    val size: String,
    val stock: Int,
)
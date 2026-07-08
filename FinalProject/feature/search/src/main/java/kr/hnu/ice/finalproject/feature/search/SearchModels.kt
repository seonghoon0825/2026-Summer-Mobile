package kr.hnu.ice.finalproject.feature.search

/** 정렬 옵션. */
enum class SortOption(val label: String) {
    POPULAR("인기순"),
    PRICE_LOW("가격 낮은순"),
    PRICE_HIGH("가격 높은순"),
    LATEST("최신순"),
}

/** 가격대 필터 구간. */
enum class PriceRange(val label: String, val min: Int, val max: Int) {
    ALL("전체", 0, Int.MAX_VALUE),
    UNDER_30K("3만원 이하", 0, 30_000),
    FROM_30K_TO_70K("3~7만원", 30_000, 70_000),
    FROM_70K_TO_150K("7~15만원", 70_000, 150_000),
    OVER_150K("15만원 이상", 150_000, Int.MAX_VALUE),
}

/** 검색 필터 상태. (선택된 브랜드/가격대/사이즈) */
data class SearchFilter(
    val brands: Set<String> = emptySet(),
    val priceRange: PriceRange = PriceRange.ALL,
    val sizes: Set<String> = emptySet(),
) {
    /** 기본값과 다르면 필터가 적용된 상태. */
    val isActive: Boolean
        get() = brands.isNotEmpty() || priceRange != PriceRange.ALL || sizes.isNotEmpty()
}

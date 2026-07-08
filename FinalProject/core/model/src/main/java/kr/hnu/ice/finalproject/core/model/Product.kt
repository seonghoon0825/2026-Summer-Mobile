package kr.hnu.ice.finalproject.core.model

/**
 * 상품 도메인 모델.
 *
 * @param id 상품 식별자
 * @param name 상품명
 * @param brand 브랜드명
 * @param price 판매가 (원, KRW 단위의 정수)
 * @param imageUrl 대표 이미지 URL
 * @param category 소속 카테고리
 * @param options 선택 가능한 옵션 목록 (색상/사이즈/재고)
 * @param rating 평균 평점 (0.0 ~ 5.0)
 * @param reviewCount 리뷰 개수
 * @param description 상품 상세 설명
 */
data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val price: Int,
    val imageUrl: String,
    val category: Category,
    val options: List<ProductOption>,
    val rating: Double,
    val reviewCount: Int,
    val description: String,
)
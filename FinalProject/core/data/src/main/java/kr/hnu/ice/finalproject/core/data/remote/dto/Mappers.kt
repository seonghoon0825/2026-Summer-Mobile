package kr.hnu.ice.finalproject.core.data.remote.dto

import kr.hnu.ice.finalproject.core.model.Category
import kr.hnu.ice.finalproject.core.model.Product
import kr.hnu.ice.finalproject.core.model.ProductOption
import kr.hnu.ice.finalproject.core.model.Review

// ---- DTO -> 도메인 모델 매핑 ----
// 상위 계층에는 도메인 모델만 노출되고, DTO는 core:data 안에 숨겨진다.

fun ProductOptionDto.toDomain(): ProductOption = ProductOption(
    color = color,
    size = size,
    stock = stock,
)

fun CategoryDto.toDomain(): Category = Category(
    id = id,
    name = name,
)

/**
 * ProductDto를 Product로 변환한다.
 * categoryId를 실제 Category 객체로 치환하기 위해 카테고리 맵을 함께 받는다.
 * (맵에 없으면 id를 이름으로 갖는 임시 Category로 대체)
 */
fun ProductDto.toDomain(categoryMap: Map<String, Category>): Product {
    // salePrice가 정상가보다 낮으면 세일 중: price=세일가, originalPrice=정상가로 매핑.
    val onSale = salePrice != null && salePrice < price
    return Product(
        id = id,
        name = name,
        brand = brand,
        price = if (onSale) salePrice!! else price,
        imageUrl = imageUrl,
        category = categoryMap[categoryId] ?: Category(categoryId, categoryId),
        options = options.map { it.toDomain() },
        rating = rating,
        reviewCount = reviewCount,
        description = description,
        originalPrice = if (onSale) price else null,
    )
}

fun ReviewDto.toDomain(): Review = Review(
    id = id,
    productId = productId,
    rating = rating,
    content = content,
    imageUrl = imageUrl,
    author = author,
)
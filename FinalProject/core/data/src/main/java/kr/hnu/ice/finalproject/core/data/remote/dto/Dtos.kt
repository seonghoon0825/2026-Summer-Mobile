package kr.hnu.ice.finalproject.core.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * assets JSON(Mock) 파싱용 DTO들.
 * 도메인 모델(core:model)과 분리해 두어, 나중에 서버 API 응답으로 바뀌어도
 * 도메인 모델과 상위 계층은 영향받지 않는다. (DTO만 교체 + 매퍼 수정)
 */

@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    val brand: String,
    val price: Int,
    val imageUrl: String,
    val categoryId: String,
    val rating: Double,
    val reviewCount: Int,
    val description: String,
    val options: List<ProductOptionDto>,
    // 세일 중이면 세일가(원). 없으면 정상가. (Mock 데이터에 세일 상태를 넣기 위한 필드)
    val salePrice: Int? = null,
)

@Serializable
data class ProductOptionDto(
    val color: String,
    val size: String,
    val stock: Int,
)

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
)

@Serializable
data class ReviewDto(
    val id: String,
    val productId: String,
    val rating: Int,
    val content: String,
    val imageUrl: String? = null,
    val author: String,
)
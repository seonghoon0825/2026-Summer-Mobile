package kr.hnu.ice.finalproject.core.model

/**
 * 상품 카테고리.
 *
 * @param id 카테고리 식별자
 * @param name 카테고리 이름 (예: "상의", "아우터")
 */
data class Category(
    val id: String,
    val name: String,
)
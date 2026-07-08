package kr.hnu.ice.finalproject.core.model

/**
 * 상품 리뷰.
 *
 * @param id 리뷰 식별자
 * @param productId 리뷰 대상 상품의 id
 * @param rating 평점 (1 ~ 5)
 * @param content 리뷰 본문
 * @param imageUrl 첨부 이미지 URL (없을 수 있음)
 * @param author 작성자 이름/닉네임
 */
data class Review(
    val id: String,
    val productId: String,
    val rating: Int,
    val content: String,
    val imageUrl: String?,
    val author: String,
)
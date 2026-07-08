package kr.hnu.ice.finalproject.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 장바구니 항목. 상품 전체가 아니라 productId + 선택 옵션(color/size) + 수량만 저장한다.
 * 상품 상세 정보는 읽을 때 ProductRepository로 복원한다(단일 진실 원천).
 * (productId + color + size) 조합을 복합 기본키로 사용해 같은 옵션은 하나만 존재하게 한다.
 */
@Entity(tableName = "cart_items", primaryKeys = ["productId", "color", "size"])
data class CartItemEntity(
    val productId: String,
    val color: String,
    val size: String,
    val quantity: Int,
    val addedAt: Long,
)

/** 찜 항목. 상품당 하나만 존재. */
@Entity(tableName = "wish_items")
data class WishItemEntity(
    @PrimaryKey val productId: String,
    val addedAt: Long,
)

/** 최근 본 상품. viewedAt으로 정렬해 최신순 노출. */
@Entity(tableName = "recent_products")
data class RecentProductEntity(
    @PrimaryKey val productId: String,
    val viewedAt: Long,
)

/** 최근 검색어. keyword를 기본키로 삼아 중복 검색 시 시각만 갱신. */
@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val keyword: String,
    val searchedAt: Long,
)

/**
 * 주문. 주문 항목은 구매 시점 스냅샷을 JSON 문자열(itemsJson)로 저장한다.
 * (상품 가격/옵션이 나중에 바뀌어도 주문 내역은 그대로 보존)
 */
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val totalPrice: Int,
    val status: String,
    val orderedAt: Long,
    val address: String,
    val itemsJson: String,
)
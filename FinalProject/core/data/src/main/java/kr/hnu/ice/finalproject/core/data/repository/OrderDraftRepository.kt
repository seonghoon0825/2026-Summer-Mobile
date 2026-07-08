package kr.hnu.ice.finalproject.core.data.repository

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 장바구니에서 주문으로 넘기는 스냅샷.
 *
 * @param selectedKeys 주문할 항목 키들(CartItem.key: 상품+옵션)
 * @param couponDiscount 장바구니에서 적용한 쿠폰 할인액(원)
 */
data class OrderDraft(
    val selectedKeys: Set<String>,
    val couponDiscount: Int,
)

/**
 * 주문 초안 저장소(인메모리). 장바구니가 "주문하기" 시점의 선택/쿠폰을 기록하고,
 * 주문 흐름(OrderViewModel)이 읽어 동일한 항목·금액으로 주문서를 구성한다.
 * feature:cart ↔ feature:order 간 직접 의존 없이 상태를 전달하기 위한 창구다.
 */
interface OrderDraftRepository {
    fun setDraft(draft: OrderDraft)
    fun getDraft(): OrderDraft?

    /** 주문 완료(결제 성공) 후 초안 제거. */
    fun clearDraft()
}

@Singleton
class OrderDraftRepositoryImpl @Inject constructor() : OrderDraftRepository {

    private var draft: OrderDraft? = null

    override fun setDraft(draft: OrderDraft) {
        this.draft = draft
    }

    override fun getDraft(): OrderDraft? = draft

    override fun clearDraft() {
        draft = null
    }
}
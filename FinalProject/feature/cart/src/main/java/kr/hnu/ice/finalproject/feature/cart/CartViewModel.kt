package kr.hnu.ice.finalproject.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.hnu.ice.finalproject.core.data.repository.CartRepository
import kr.hnu.ice.finalproject.core.data.repository.OrderDraft
import kr.hnu.ice.finalproject.core.data.repository.OrderDraftRepository
import kr.hnu.ice.finalproject.core.model.CartItem
import kr.hnu.ice.finalproject.core.model.ProductOption
import javax.inject.Inject

/** 금액 요약. */
data class CartSummary(
    val subtotal: Int = 0,
    val couponDiscount: Int = 0,
    val shippingFee: Int = 0,
    val rewardPoints: Int = 0,
    val total: Int = 0,
    val selectedCount: Int = 0,
)

/** 장바구니 화면 상태. */
data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val selectedKeys: Set<String> = emptySet(),
    val summary: CartSummary = CartSummary(),
    val availableCoupons: List<Coupon> = emptyList(),
    val selectedCoupon: Coupon? = null,
    val isLoading: Boolean = true,
) {
    val isAllSelected: Boolean get() = items.isNotEmpty() && selectedKeys.size == items.size
}

/**
 * 장바구니 ViewModel.
 * CartRepository의 Flow를 단일 소스로 구독하므로, 상세에서 담은 항목이 즉시 반영된다.
 * 선택 상태는 화면 로컬 상태(_deselectedKeys)로 관리한다(기본: 전체 선택).
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderDraftRepository: OrderDraftRepository,
) : ViewModel() {

    // 기본은 전체 선택 → 사용자가 해제한 키만 저장한다(새로 담긴 항목은 자동 선택됨).
    private val _deselectedKeys = MutableStateFlow<Set<String>>(emptySet())

    // 적용한 쿠폰(없으면 null)
    private val _selectedCoupon = MutableStateFlow<Coupon?>(null)

    // 액션에서 참조하기 위한 최신 목록 스냅샷
    private var latestItems: List<CartItem> = emptyList()

    val uiState: StateFlow<CartUiState> = combine(
        cartRepository.getCartItems(),
        _deselectedKeys,
        _selectedCoupon,
    ) { items, deselected, coupon ->
        latestItems = items
        val allKeys = items.map { it.key }.toSet()
        val effectiveDeselected = deselected intersect allKeys
        val selectedKeys = allKeys - effectiveDeselected
        val selectedItems = items.filter { it.key in selectedKeys }
        CartUiState(
            items = items,
            selectedKeys = selectedKeys,
            summary = calculateSummary(selectedItems, coupon),
            availableCoupons = Coupon.MOCK_COUPONS,
            selectedCoupon = coupon,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartUiState(),
    )

    /** 쿠폰 적용/해제(실시간으로 금액이 재계산된다). */
    fun selectCoupon(coupon: Coupon?) {
        _selectedCoupon.value = coupon
    }

    /**
     * 주문하기 직전에 호출: 현재 선택 항목/쿠폰 할인액을 주문 초안으로 기록한다.
     * 주문 흐름(OrderViewModel)이 이 초안을 읽어 같은 항목·금액으로 주문서를 만든다.
     */
    fun prepareOrder() {
        val state = uiState.value
        orderDraftRepository.setDraft(
            OrderDraft(
                selectedKeys = state.selectedKeys,
                couponDiscount = state.summary.couponDiscount,
            ),
        )
    }

    // ---- 선택 ----
    fun toggleSelect(key: String) {
        _deselectedKeys.update { if (key in it) it - key else it + key }
    }

    fun selectAll() {
        _deselectedKeys.value = emptySet()
    }

    fun deselectAll() {
        _deselectedKeys.value = latestItems.map { it.key }.toSet()
    }

    // ---- 수량 ----
    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            cartRepository.updateQuantity(item.product.id, item.selectedOption, item.quantity + 1)
        }
    }

    fun decreaseQuantity(item: CartItem) {
        if (item.quantity <= 1) return
        viewModelScope.launch {
            cartRepository.updateQuantity(item.product.id, item.selectedOption, item.quantity - 1)
        }
    }

    // ---- 옵션 변경 (기존 항목 삭제 후 새 옵션으로 다시 담기) ----
    fun changeOption(item: CartItem, newOption: ProductOption) {
        if (newOption == item.selectedOption) return
        viewModelScope.launch {
            cartRepository.removeFromCart(item.product.id, item.selectedOption)
            cartRepository.addToCart(item.product, newOption, item.quantity)
        }
    }

    // ---- 삭제 ----
    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            cartRepository.removeFromCart(item.product.id, item.selectedOption)
        }
    }

    fun removeSelected() {
        val deselected = _deselectedKeys.value
        val selected = latestItems.filter { it.key !in deselected }
        viewModelScope.launch {
            selected.forEach { cartRepository.removeFromCart(it.product.id, it.selectedOption) }
        }
    }

    private fun calculateSummary(selectedItems: List<CartItem>, coupon: Coupon?): CartSummary {
        val subtotal = selectedItems.sumOf { it.lineTotal }
        // 쿠폰 할인(정액/정률). 사용 불가 쿠폰은 0원.
        val couponDiscount = coupon?.discountFor(subtotal) ?: 0
        val shippingFee = if (subtotal == 0 || subtotal >= FREE_SHIPPING_THRESHOLD) 0 else SHIPPING_FEE
        // 쿠폰 적용 후 상품 금액 기준으로 적립(1%) 예정 금액 계산
        val discountedSubtotal = subtotal - couponDiscount
        val rewardPoints = (discountedSubtotal * REWARD_RATE).toInt()
        return CartSummary(
            subtotal = subtotal,
            couponDiscount = couponDiscount,
            shippingFee = shippingFee,
            rewardPoints = rewardPoints,
            total = discountedSubtotal + shippingFee,
            selectedCount = selectedItems.size,
        )
    }

    private companion object {
        const val FREE_SHIPPING_THRESHOLD = 50_000
        const val SHIPPING_FEE = 3_000
        const val REWARD_RATE = 0.01 // 적립률 1%
    }
}

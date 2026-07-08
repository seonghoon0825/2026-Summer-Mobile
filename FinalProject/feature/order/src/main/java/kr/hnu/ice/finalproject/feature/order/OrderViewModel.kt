package kr.hnu.ice.finalproject.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.hnu.ice.finalproject.core.data.repository.CartRepository
import kr.hnu.ice.finalproject.core.data.repository.OrderDraftRepository
import kr.hnu.ice.finalproject.core.data.repository.OrderRepository
import kr.hnu.ice.finalproject.core.model.CartItem
import kr.hnu.ice.finalproject.core.model.Order
import javax.inject.Inject

/** 결제수단(시뮬레이션). */
enum class PaymentMethod(val label: String) {
    CARD("신용/체크카드"),
    BANK("무통장입금"),
    SIMPLE("간편결제"),
}

/** 결제 진행 상태. */
sealed interface PaymentState {
    data object Idle : PaymentState
    data object Processing : PaymentState
    data object Success : PaymentState
}

/** 주문 흐름 전체가 공유하는 상태. */
data class OrderUiState(
    val items: List<CartItem> = emptyList(),
    val recipient: String = "",
    val phone: String = "",
    val address: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.CARD,
    val isLoading: Boolean = true,
    val subtotal: Int = 0,
    val discount: Int = 0,
    val shippingFee: Int = 0,
    val total: Int = 0,
    val paymentState: PaymentState = PaymentState.Idle,
    val createdOrder: Order? = null,
) {
    /** 배송지 입력이 모두 채워졌고 주문할 상품이 있는지. */
    val canPay: Boolean
        get() = items.isNotEmpty() && recipient.isNotBlank() && phone.isNotBlank() && address.isNotBlank()
}

/**
 * 주문 ViewModel. 주문서→결제→완료 화면이 공유한다(중첩 그래프에 스코프).
 * 장바구니가 기록한 주문 초안(선택 항목/쿠폰 할인)을 읽어 동일한 구성·금액으로 주문서를 만들고,
 * 결제는 실제 없이 2초 지연으로 시뮬레이션한다. 성공 시 주문 저장 + 주문한 항목만 장바구니에서 제거.
 */
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val orderDraftRepository: OrderDraftRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        // 주문 흐름 진입 시점의 장바구니를 스냅샷으로 로드(이후 장바구니가 비워져도 유지).
        // 초안이 있으면 장바구니에서 선택한 항목만 담고, 쿠폰 할인액을 그대로 승계한다.
        viewModelScope.launch {
            val draft = orderDraftRepository.getDraft()
            val allItems = cartRepository.getCartItems().first()
            val items = draft?.selectedKeys?.let { keys -> allItems.filter { it.key in keys } } ?: allItems
            _uiState.update {
                it.copy(items = items, discount = draft?.couponDiscount ?: 0, isLoading = false)
            }
            recalculate()
        }
    }

    fun onRecipientChange(value: String) = _uiState.update { it.copy(recipient = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onAddressChange(value: String) = _uiState.update { it.copy(address = value) }
    fun onPaymentMethodChange(method: PaymentMethod) = _uiState.update { it.copy(paymentMethod = method) }

    /** 결제 시뮬레이션: 2초 로딩 후 주문 저장 + 주문한 항목만 장바구니에서 제거 → 성공. */
    fun pay() {
        val state = _uiState.value
        if (!state.canPay || state.paymentState != PaymentState.Idle) return
        viewModelScope.launch {
            _uiState.update { it.copy(paymentState = PaymentState.Processing) }
            delay(PAYMENT_DELAY_MS) // 가짜 결제 처리 시간

            val fullAddress = "${state.address} (${state.recipient}, ${state.phone})"
            val order = orderRepository.createOrder(items = state.items, address = fullAddress)
            // 전체 비우기가 아니라 주문한 항목만 제거(선택 안 한 항목은 장바구니에 남는다)
            state.items.forEach { cartRepository.removeFromCart(it.product.id, it.selectedOption) }
            orderDraftRepository.clearDraft()

            _uiState.update { it.copy(paymentState = PaymentState.Success, createdOrder = order) }
        }
    }

    private fun recalculate() {
        _uiState.update { state ->
            val subtotal = state.items.sumOf { it.lineTotal }
            val shipping = if (subtotal == 0 || subtotal >= FREE_SHIPPING_THRESHOLD) 0 else SHIPPING_FEE
            // 할인은 장바구니에서 승계한 쿠폰 할인액(state.discount)을 그대로 사용한다.
            state.copy(
                subtotal = subtotal,
                shippingFee = shipping,
                total = subtotal - state.discount + shipping,
            )
        }
    }

    private companion object {
        const val PAYMENT_DELAY_MS = 2_000L
        const val FREE_SHIPPING_THRESHOLD = 50_000
        const val SHIPPING_FEE = 3_000
    }
}

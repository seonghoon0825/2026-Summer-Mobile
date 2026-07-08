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
 * 결제는 실제 없이 2초 지연으로 시뮬레이션하고, 성공 시 OrderRepository로 저장 + 장바구니 비우기.
 */
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        // 주문 흐름 진입 시점의 장바구니를 스냅샷으로 로드(이후 장바구니가 비워져도 유지)
        viewModelScope.launch {
            val items = cartRepository.getCartItems().first()
            _uiState.update { it.copy(items = items, isLoading = false) }
            recalculate()
        }
    }

    fun onRecipientChange(value: String) = _uiState.update { it.copy(recipient = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onAddressChange(value: String) = _uiState.update { it.copy(address = value) }
    fun onPaymentMethodChange(method: PaymentMethod) = _uiState.update { it.copy(paymentMethod = method) }

    /** 결제 시뮬레이션: 2초 로딩 후 주문 저장 + 장바구니 비우기 → 성공. */
    fun pay() {
        val state = _uiState.value
        if (!state.canPay || state.paymentState != PaymentState.Idle) return
        viewModelScope.launch {
            _uiState.update { it.copy(paymentState = PaymentState.Processing) }
            delay(PAYMENT_DELAY_MS) // 가짜 결제 처리 시간

            val fullAddress = "${state.address} (${state.recipient}, ${state.phone})"
            val order = orderRepository.createOrder(items = state.items, address = fullAddress)
            cartRepository.clearCart()

            _uiState.update { it.copy(paymentState = PaymentState.Success, createdOrder = order) }
        }
    }

    private fun recalculate() {
        _uiState.update { state ->
            val subtotal = state.items.sumOf { it.lineTotal }
            val discount = (subtotal * 0.05).toInt()
            val shipping = if (subtotal == 0 || subtotal >= FREE_SHIPPING_THRESHOLD) 0 else SHIPPING_FEE
            state.copy(
                subtotal = subtotal,
                discount = discount,
                shippingFee = shipping,
                total = subtotal - discount + shipping,
            )
        }
    }

    private companion object {
        const val PAYMENT_DELAY_MS = 2_000L
        const val FREE_SHIPPING_THRESHOLD = 50_000
        const val SHIPPING_FEE = 3_000
    }
}

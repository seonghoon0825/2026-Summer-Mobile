package kr.hnu.ice.finalproject.feature.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.hnu.ice.finalproject.core.common.UiState
import kr.hnu.ice.finalproject.core.data.repository.OrderRepository
import kr.hnu.ice.finalproject.core.model.Order
import javax.inject.Inject

/** 주문 내역. OrderRepository의 Flow를 구독한다. */
@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : ViewModel() {

    val uiState: StateFlow<UiState<List<Order>>> = orderRepository.getOrders()
        .map { UiState.Success(it) as UiState<List<Order>> }
        .catch { e -> emit(UiState.Error(e.message ?: "주문 내역을 불러오지 못했어요.", e)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    /** Mock: 배송 상태를 다음 단계로 진행한다. getOrders Flow가 갱신되어 타임라인이 즉시 반영된다. */
    fun advanceStatus(orderId: String) {
        viewModelScope.launch { orderRepository.advanceOrderStatus(orderId) }
    }
}
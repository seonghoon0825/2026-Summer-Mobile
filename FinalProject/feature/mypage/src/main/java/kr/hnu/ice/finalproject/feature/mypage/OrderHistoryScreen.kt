package kr.hnu.ice.finalproject.feature.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.common.UiState
import kr.hnu.ice.finalproject.core.common.util.DateFormatter
import kr.hnu.ice.finalproject.core.common.util.PriceFormatter
import kr.hnu.ice.finalproject.core.designsystem.component.EmptyView
import kr.hnu.ice.finalproject.core.designsystem.component.ErrorView
import kr.hnu.ice.finalproject.core.designsystem.component.LoadingIndicator
import kr.hnu.ice.finalproject.core.designsystem.component.StepTimeline
import kr.hnu.ice.finalproject.core.model.Order
import kr.hnu.ice.finalproject.core.model.OrderStatus

// 배송 단계 라벨(OrderStatus ordinal 순서와 일치: ORDERED/PAID/SHIPPING/DELIVERED)
private val STATUS_LABELS = listOf("주문완료", "결제완료", "배송중", "배송완료")

/** 주문 내역 화면. 주문 카드 + 배송 상태 타임라인(Mock 상태 전환 포함). */
@Composable
fun OrderHistoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrderHistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        MyPageTopBar(title = "주문 내역", onBack = onBack)

        when (val state = uiState) {
            UiState.Loading -> LoadingIndicator()
            is UiState.Error -> ErrorView(message = state.message)
            is UiState.Success -> {
                val orders = state.data
                if (orders.isEmpty()) {
                    EmptyView(message = "주문 내역이 없어요.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(orders, key = { it.id }) { order ->
                            OrderCard(order = order, onAdvance = { viewModel.advanceStatus(order.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onAdvance: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = DateFormatter.format(order.orderedAt, DateFormatter.DATE_TIME_PATTERN),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val first = order.items.firstOrNull()
            val summary = when {
                first == null -> "주문 상품 없음"
                order.items.size == 1 -> "${first.product.brand} ${first.product.name}"
                else -> "${first.product.name} 외 ${order.items.size - 1}건"
            }
            Text(text = summary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(
                text = PriceFormatter.format(order.totalPrice),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 배송 상태 타임라인 (현재 단계 = OrderStatus ordinal)
            StepTimeline(steps = STATUS_LABELS, currentStep = order.status.ordinal)

            // 현재 상태 + Mock 상태 전환 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "현재 상태: ${STATUS_LABELS[order.status.ordinal]}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (order.status != OrderStatus.DELIVERED) {
                    TextButton(onClick = onAdvance) { Text("배송 단계 진행 ›") }
                } else {
                    Text(
                        text = "배송 완료",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
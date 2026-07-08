package kr.hnu.ice.finalproject.feature.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.common.util.PriceFormatter
import kr.hnu.ice.finalproject.core.designsystem.component.AppButton
import kr.hnu.ice.finalproject.core.model.CartItem

/**
 * 주문서 화면. 배송지 입력 + 주문 상품 확인 + 결제수단 선택 + 최종 금액.
 *
 * @param viewModel 주문 흐름 공유 ViewModel(app이 그래프 스코프로 제공)
 * @param onProceedToPayment 결제 화면으로 이동
 * @param onBack 뒤로가기
 */
@Composable
fun OrderScreen(
    viewModel: OrderViewModel,
    onProceedToPayment: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        OrderTopBar(title = "주문서", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 배송지
            SectionTitle("배송지")
            OutlinedTextField(
                value = state.recipient,
                onValueChange = viewModel::onRecipientChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("받는 분") },
                singleLine = true,
            )
            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("연락처") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )
            OutlinedTextField(
                value = state.address,
                onValueChange = viewModel::onAddressChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("주소") },
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 주문 상품
            SectionTitle("주문 상품 (${state.items.size})")
            state.items.forEach { item -> OrderItemRow(item) }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 결제수단
            SectionTitle("결제수단")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentMethod.entries.forEach { method ->
                    FilterChip(
                        selected = state.paymentMethod == method,
                        onClick = { viewModel.onPaymentMethodChange(method) },
                        label = { Text(method.label) },
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 금액
            SectionTitle("결제 금액")
            AmountLine("상품 금액", PriceFormatter.format(state.subtotal))
            AmountLine("쿠폰 할인", "-${PriceFormatter.format(state.discount)}")
            AmountLine("배송비", if (state.shippingFee == 0) "무료" else PriceFormatter.format(state.shippingFee))
        }

        // 하단 결제 버튼
        Surface(shadowElevation = 8.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                AppButton(
                    text = "${PriceFormatter.format(state.total)} 결제하기",
                    onClick = onProceedToPayment,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.canPay,
                )
            }
        }
    }
}

@Composable
internal fun OrderTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
        }
        Text(text = title, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun OrderItemRow(item: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.product.brand, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(
                text = "${item.product.name} · ${item.selectedOption.color}/${item.selectedOption.size} · ${item.quantity}개",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(text = PriceFormatter.format(item.lineTotal), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun AmountLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
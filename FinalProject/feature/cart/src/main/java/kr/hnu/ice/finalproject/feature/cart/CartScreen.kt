package kr.hnu.ice.finalproject.feature.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.common.util.PriceFormatter
import kr.hnu.ice.finalproject.core.designsystem.component.AppButton
import kr.hnu.ice.finalproject.core.designsystem.component.EmptyView
import kr.hnu.ice.finalproject.core.designsystem.component.NetworkImage
import kr.hnu.ice.finalproject.core.designsystem.component.PriceText
import kr.hnu.ice.finalproject.core.model.CartItem

/**
 * 장바구니 화면. CartRepository를 단일 소스로 담긴 상품을 실시간 표시하고,
 * 수량/옵션 변경, 선택 삭제, 금액 요약, 주문하기를 제공한다.
 *
 * @param onOrderClick 주문하기 → 주문 화면으로 이동(app이 처리)
 */
@Composable
fun CartScreen(
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editingItem by remember { mutableStateOf<CartItem?>(null) }
    var showCouponSheet by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        if (state.items.isEmpty()) {
            EmptyView(message = "장바구니가 비어 있어요.", modifier = Modifier.weight(1f))
        } else {
            // 전체선택 / 선택삭제
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.isAllSelected,
                    onCheckedChange = { checked ->
                        if (checked) viewModel.selectAll() else viewModel.deselectAll()
                    },
                )
                Text("전체 선택 (${state.selectedKeys.size}/${state.items.size})")
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = viewModel::removeSelected,
                    enabled = state.selectedKeys.isNotEmpty(),
                ) {
                    Text("선택 삭제")
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(state.items, key = { it.key }) { item ->
                    CartItemRow(
                        item = item,
                        selected = item.key in state.selectedKeys,
                        onToggleSelect = { viewModel.toggleSelect(item.key) },
                        onIncrease = { viewModel.increaseQuantity(item) },
                        onDecrease = { viewModel.decreaseQuantity(item) },
                        onRemove = { viewModel.removeItem(item) },
                        onChangeOption = { editingItem = item },
                    )
                }
            }
        }

        CartSummarySection(
            summary = state.summary,
            selectedCoupon = state.selectedCoupon,
            onCouponClick = { showCouponSheet = true },
            // 주문 직전 선택 항목/쿠폰을 초안으로 기록한 뒤 주문 화면으로 이동
            onOrderClick = {
                viewModel.prepareOrder()
                onOrderClick()
            },
        )
    }

    // 옵션 변경 시트
    editingItem?.let { item ->
        ChangeOptionSheet(
            cartItem = item,
            onConfirm = { newOption ->
                viewModel.changeOption(item, newOption)
                editingItem = null
            },
            onDismiss = { editingItem = null },
        )
    }

    // 쿠폰 선택 시트
    if (showCouponSheet) {
        CouponBottomSheet(
            availableCoupons = state.availableCoupons,
            selectedCoupon = state.selectedCoupon,
            subtotal = state.summary.subtotal,
            onSelect = viewModel::selectCoupon,
            onDismiss = { showCouponSheet = false },
        )
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    selected: Boolean,
    onToggleSelect: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    onChangeOption: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Checkbox(checked = selected, onCheckedChange = { onToggleSelect() })
        NetworkImage(
            imageUrl = item.product.imageUrl,
            contentDescription = item.product.name,
            modifier = Modifier
                .size(width = 80.dp, height = 100.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.product.brand,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "삭제")
                }
            }
            Text(
                text = item.product.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // 옵션 + 변경 버튼
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${item.selectedOption.color} / ${item.selectedOption.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onChangeOption) { Text("옵션 변경") }
            }
            // 수량 + 금액
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease, enabled = item.quantity > 1, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Remove, contentDescription = "수량 감소")
                    }
                    Text(text = "${item.quantity}", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Add, contentDescription = "수량 증가")
                    }
                }
                PriceText(price = item.lineTotal)
            }
        }
    }
}

@Composable
private fun CartSummarySection(
    summary: CartSummary,
    selectedCoupon: Coupon?,
    onCouponClick: () -> Unit,
    onOrderClick: () -> Unit,
) {
    Surface(shadowElevation = 8.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // 쿠폰 선택
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCouponClick)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("쿠폰", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedCoupon?.name ?: "쿠폰 선택",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedCoupon != null) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            SummaryLine("상품 금액", PriceFormatter.format(summary.subtotal))
            SummaryLine("쿠폰 할인", "-${PriceFormatter.format(summary.couponDiscount)}")
            SummaryLine(
                "배송비",
                if (summary.shippingFee == 0) "무료" else PriceFormatter.format(summary.shippingFee),
            )
            SummaryLine("적립 예정", "+${PriceFormatter.format(summary.rewardPoints)}")
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("최종 결제 예상액", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                PriceText(price = summary.total, style = MaterialTheme.typography.titleLarge, emphasize = true)
            }
            AppButton(
                text = "주문하기 (${summary.selectedCount}개)",
                onClick = onOrderClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = summary.selectedCount > 0,
            )
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
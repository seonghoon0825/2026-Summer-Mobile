package kr.hnu.ice.finalproject.feature.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.hnu.ice.finalproject.core.common.util.PriceFormatter

/**
 * 쿠폰 선택 바텀시트. 현재 상품 금액(subtotal) 기준으로 사용 가능 여부와 할인액을 미리 보여준다.
 *
 * @param onSelect 선택 결과(null이면 쿠폰 미적용)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponBottomSheet(
    availableCoupons: List<Coupon>,
    selectedCoupon: Coupon?,
    subtotal: Int,
    onSelect: (Coupon?) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = "쿠폰 선택",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp),
            )

            // 쿠폰 미적용
            CouponRow(
                title = "쿠폰 미적용",
                subtitle = null,
                selected = selectedCoupon == null,
                enabled = true,
                onClick = {
                    onSelect(null)
                    onDismiss()
                },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            availableCoupons.forEach { coupon ->
                val applicable = coupon.isApplicable(subtotal)
                val subtitle = if (applicable) {
                    "-${PriceFormatter.format(coupon.discountFor(subtotal))} 할인"
                } else {
                    "${PriceFormatter.format(coupon.minOrderAmount)} 이상 구매 시 사용 가능"
                }
                CouponRow(
                    title = coupon.name,
                    subtitle = subtitle,
                    selected = selectedCoupon?.id == coupon.id,
                    enabled = applicable,
                    onClick = {
                        onSelect(coupon)
                        onDismiss()
                    },
                )
            }
        }
    }
}

@Composable
private fun CouponRow(
    title: String,
    subtitle: String?,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick, enabled = enabled)
        Column(
            modifier = Modifier.padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
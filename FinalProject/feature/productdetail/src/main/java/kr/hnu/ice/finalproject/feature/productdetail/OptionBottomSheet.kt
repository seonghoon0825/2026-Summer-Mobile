package kr.hnu.ice.finalproject.feature.productdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.hnu.ice.finalproject.core.designsystem.component.AppButton
import kr.hnu.ice.finalproject.core.designsystem.component.PriceText
import kr.hnu.ice.finalproject.core.model.Product
import kr.hnu.ice.finalproject.core.model.ProductOption

/**
 * 옵션(색상/사이즈) 선택 바텀시트. 선택 완료 시 수량과 함께 장바구니에 담는다.
 *
 * @param onAddToCart 담기 확정 콜백(선택 옵션 + 수량)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionBottomSheet(
    product: Product,
    onAddToCart: (ProductOption, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val colors = remember(product) { product.options.map { it.color }.distinct() }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var selectedSize by remember { mutableStateOf<String?>(null) }
    var quantity by remember { mutableStateOf(1) }

    // 선택한 색상에서 고를 수 있는 사이즈
    val sizesForColor = remember(selectedColor, product) {
        product.options.filter { it.color == selectedColor }.map { it.size }
    }
    // 최종 선택된 옵션(색상+사이즈 모두 정해졌을 때)
    val selectedOption: ProductOption? = product.options.firstOrNull {
        it.color == selectedColor && it.size == selectedSize
    }
    val canAdd = selectedOption != null && selectedOption.stock > 0

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)

            // 색상
            SectionLabel("색상")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colors.forEach { color ->
                    FilterChip(
                        selected = selectedColor == color,
                        onClick = {
                            selectedColor = color
                            selectedSize = null // 색상 바꾸면 사이즈 초기화
                        },
                        label = { Text(color) },
                    )
                }
            }

            // 사이즈 (색상 선택 후 노출)
            if (selectedColor != null) {
                SectionLabel("사이즈")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    sizesForColor.forEach { size ->
                        val option = product.options.first { it.color == selectedColor && it.size == size }
                        val soldOut = option.stock <= 0
                        FilterChip(
                            selected = selectedSize == size,
                            enabled = !soldOut,
                            onClick = { selectedSize = size },
                            label = { Text(if (soldOut) "$size (품절)" else size) },
                        )
                    }
                }
            }

            // 수량 (옵션 확정 후)
            if (selectedOption != null) {
                SectionLabel("수량")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        enabled = quantity > 1,
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = "수량 감소")
                    }
                    Text(text = "$quantity", style = MaterialTheme.typography.titleMedium)
                    IconButton(
                        onClick = { if (quantity < selectedOption.stock) quantity++ },
                        enabled = quantity < selectedOption.stock,
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "수량 증가")
                    }
                    Text(
                        text = "(재고 ${selectedOption.stock})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }

            // 합계 + 담기
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PriceText(price = product.price * quantity)
                AppButton(
                    text = "장바구니 담기",
                    onClick = { selectedOption?.let { onAddToCart(it, quantity) } },
                    enabled = canAdd,
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp),
    )
}
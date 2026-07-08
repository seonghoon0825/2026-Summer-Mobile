package kr.hnu.ice.finalproject.feature.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.hnu.ice.finalproject.core.designsystem.component.AppButton
import kr.hnu.ice.finalproject.core.model.CartItem
import kr.hnu.ice.finalproject.core.model.ProductOption

/**
 * 장바구니 항목의 옵션(색상/사이즈)을 변경하는 바텀시트.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeOptionSheet(
    cartItem: CartItem,
    onConfirm: (ProductOption) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val product = cartItem.product

    val colors = remember(product) { product.options.map { it.color }.distinct() }
    var selectedColor by remember { mutableStateOf(cartItem.selectedOption.color) }
    var selectedSize by remember { mutableStateOf(cartItem.selectedOption.size) }

    val sizesForColor = product.options.filter { it.color == selectedColor }.map { it.size }
    val selectedOption = product.options.firstOrNull {
        it.color == selectedColor && it.size == selectedSize
    }
    val canConfirm = selectedOption != null && selectedOption.stock > 0

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("옵션 변경", fontWeight = FontWeight.Bold)

            Text("색상", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colors.forEach { color ->
                    FilterChip(
                        selected = selectedColor == color,
                        onClick = {
                            selectedColor = color
                            // 색상 변경 시, 해당 색상의 첫 사이즈로 초기화
                            selectedSize = product.options.firstOrNull { it.color == color }?.size ?: ""
                        },
                        label = { Text(color) },
                    )
                }
            }

            Text("사이즈", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
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

            AppButton(
                text = "변경하기",
                onClick = { selectedOption?.let { onConfirm(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                enabled = canConfirm,
            )
        }
    }
}
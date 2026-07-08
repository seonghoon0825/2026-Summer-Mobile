package kr.hnu.ice.finalproject.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import kr.hnu.ice.finalproject.core.designsystem.component.AppButtonStyle

/**
 * 정렬 + 필터(가격대/브랜드/사이즈) 바텀시트.
 * 시트 안에서는 draft 상태로만 편집하고, '적용'을 눌러야 실제 반영된다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: SearchFilter,
    currentSort: SortOption,
    availableBrands: List<String>,
    availableSizes: List<String>,
    onApply: (SearchFilter, SortOption) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var draftFilter by remember { mutableStateOf(currentFilter) }
    var draftSort by remember { mutableStateOf(currentSort) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // 정렬
            SectionLabel("정렬")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SortOption.entries.forEach { option ->
                    FilterChip(
                        selected = draftSort == option,
                        onClick = { draftSort = option },
                        label = { Text(option.label) },
                    )
                }
            }

            // 가격대
            SectionLabel("가격대")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriceRange.entries.forEach { range ->
                    FilterChip(
                        selected = draftFilter.priceRange == range,
                        onClick = { draftFilter = draftFilter.copy(priceRange = range) },
                        label = { Text(range.label) },
                    )
                }
            }

            // 브랜드 (검색 결과에 존재하는 브랜드만)
            if (availableBrands.isNotEmpty()) {
                SectionLabel("브랜드")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableBrands.forEach { brand ->
                        FilterChip(
                            selected = brand in draftFilter.brands,
                            onClick = {
                                draftFilter = draftFilter.copy(brands = draftFilter.brands.toggle(brand))
                            },
                            label = { Text(brand) },
                        )
                    }
                }
            }

            // 사이즈
            if (availableSizes.isNotEmpty()) {
                SectionLabel("사이즈")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableSizes.forEach { size ->
                        FilterChip(
                            selected = size in draftFilter.sizes,
                            onClick = {
                                draftFilter = draftFilter.copy(sizes = draftFilter.sizes.toggle(size))
                            },
                            label = { Text(size) },
                        )
                    }
                }
            }

            // 액션
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppButton(
                    text = "초기화",
                    onClick = {
                        draftFilter = SearchFilter()
                        draftSort = SortOption.POPULAR
                    },
                    modifier = Modifier.weight(1f),
                    style = AppButtonStyle.Secondary,
                )
                AppButton(
                    text = "적용",
                    onClick = {
                        onApply(draftFilter, draftSort)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
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

/** 집합 토글 헬퍼: 있으면 제거, 없으면 추가. */
private fun <T> Set<T>.toggle(item: T): Set<T> =
    if (item in this) this - item else this + item
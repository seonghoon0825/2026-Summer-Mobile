package kr.hnu.ice.finalproject.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.designsystem.component.EmptyView
import kr.hnu.ice.finalproject.core.designsystem.component.LoadingIndicator
import kr.hnu.ice.finalproject.core.designsystem.component.ProductCard

/**
 * 검색 화면. 검색어 입력 + 최근 검색어 + 결과 목록 + 필터/정렬.
 *
 * @param onProductClick 상품 탭 시 호출 — 상세 이동은 app이 처리
 */
@Composable
fun SearchScreen(
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showFilterSheet by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current

    Column(modifier = modifier.fillMaxSize()) {
        SearchField(
            query = state.query,
            onQueryChange = viewModel::onQueryChange,
            onSearch = {
                viewModel.search()
                keyboard?.hide()
            },
            onClear = { viewModel.onQueryChange("") },
        )

        when {
            state.isLoading -> LoadingIndicator()

            !state.hasSearched -> RecentSearches(
                recentSearches = state.recentSearches,
                onSearchClick = {
                    viewModel.onRecentSearchClick(it)
                    keyboard?.hide()
                },
                onRemove = viewModel::removeRecentSearch,
                onClearAll = viewModel::clearRecentSearches,
            )

            else -> ResultSection(
                resultCount = state.results.size,
                sortLabel = state.sort.label,
                isFilterActive = state.filter.isActive,
                onOpenFilter = { showFilterSheet = true },
                content = {
                    if (state.results.isEmpty()) {
                        EmptyView(message = "검색 결과가 없어요.\n다른 검색어나 필터를 시도해 보세요.")
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.results, key = { it.id }) { product ->
                                ProductCard(
                                    brand = product.brand,
                                    name = product.name,
                                    price = product.price,
                                    imageUrl = product.imageUrl,
                                    rating = product.rating,
                                    reviewCount = product.reviewCount,
                                    onClick = { onProductClick(product.id) },
                                )
                            }
                        }
                    }
                },
            )
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = state.filter,
            currentSort = state.sort,
            availableBrands = state.availableBrands,
            availableSizes = state.availableSizes,
            onApply = viewModel::applyFilterAndSort,
            onDismiss = { showFilterSheet = false },
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("브랜드, 상품명 검색") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "지우기",
                    modifier = Modifier.clickable { onClear() },
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
    )
}

@Composable
private fun RecentSearches(
    recentSearches: List<String>,
    onSearchClick: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClearAll: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "최근 검색어", style = MaterialTheme.typography.titleMedium)
            if (recentSearches.isNotEmpty()) {
                TextButton(onClick = onClearAll) { Text("전체 삭제") }
            }
        }
        if (recentSearches.isEmpty()) {
            Text(
                text = "최근 검색어가 없어요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                recentSearches.forEach { keyword ->
                    InputChip(
                        selected = false,
                        onClick = { onSearchClick(keyword) },
                        label = { Text(keyword) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "삭제",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemove(keyword) },
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultSection(
    resultCount: Int,
    sortLabel: String,
    isFilterActive: Boolean,
    onOpenFilter: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${resultCount}개 · $sortLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.clickable { onOpenFilter() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "필터",
                    tint = if (isFilterActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = if (isFilterActive) "필터 적용됨" else "필터/정렬",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isFilterActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        content()
    }
}
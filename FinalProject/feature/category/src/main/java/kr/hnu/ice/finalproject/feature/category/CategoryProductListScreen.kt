package kr.hnu.ice.finalproject.feature.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.common.UiState
import kr.hnu.ice.finalproject.core.designsystem.component.EmptyView
import kr.hnu.ice.finalproject.core.designsystem.component.ErrorView
import kr.hnu.ice.finalproject.core.designsystem.component.LoadingIndicator
import kr.hnu.ice.finalproject.core.designsystem.component.ProductCard

/**
 * 선택한 카테고리의 상품 목록 화면.
 *
 * @param onProductClick 상품 탭 시 호출 — 상세 이동은 app이 처리
 * @param onBack 뒤로가기(카테고리 목록으로)
 */
@Composable
fun CategoryProductListScreen(
    onProductClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryProductListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        val title = (uiState as? UiState.Success)?.data?.categoryName ?: ""
        CategoryTopBar(title = title, onBack = onBack)

        when (val state = uiState) {
            UiState.Loading -> LoadingIndicator()
            is UiState.Error -> ErrorView(message = state.message)
            is UiState.Success -> {
                val products = state.data.products
                if (products.isEmpty()) {
                    EmptyView(message = "이 카테고리에는 상품이 없어요.")
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(products, key = { it.id }) { product ->
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
            }
        }
    }
}

/** 뒤로가기 + 카테고리명 헤더. */
@Composable
private fun CategoryTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
package kr.hnu.ice.finalproject.feature.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.common.UiState
import kr.hnu.ice.finalproject.core.designsystem.component.EmptyView
import kr.hnu.ice.finalproject.core.designsystem.component.ErrorView
import kr.hnu.ice.finalproject.core.designsystem.component.LoadingIndicator
import kr.hnu.ice.finalproject.core.designsystem.component.ProductCard

/** 찜한 상품 목록. WishRepository를 구독한다. */
@Composable
fun WishlistScreen(
    onProductClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WishlistViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        MyPageTopBar(title = "찜한 상품", onBack = onBack)

        when (val state = uiState) {
            UiState.Loading -> LoadingIndicator()
            is UiState.Error -> ErrorView(message = state.message)
            is UiState.Success -> {
                val wishItems = state.data
                if (wishItems.isEmpty()) {
                    EmptyView(message = "찜한 상품이 없어요.")
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(wishItems, key = { it.product.id }) { wish ->
                            val product = wish.product
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
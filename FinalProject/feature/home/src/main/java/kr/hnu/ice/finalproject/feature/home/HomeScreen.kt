package kr.hnu.ice.finalproject.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.common.UiState
import kr.hnu.ice.finalproject.core.designsystem.component.ErrorView
import kr.hnu.ice.finalproject.core.designsystem.component.LoadingIndicator
import kr.hnu.ice.finalproject.core.designsystem.component.NetworkImage
import kr.hnu.ice.finalproject.core.designsystem.component.PriceText
import kr.hnu.ice.finalproject.core.designsystem.component.ProductCard
import kr.hnu.ice.finalproject.core.model.Product

/**
 * 홈 화면.
 * 상단 배너(캐러셀) + 추천 상품 그리드 + 랭킹 + 최근 본 상품 자리로 구성된다.
 *
 * @param onProductClick 상품 탭 시 호출(상세 이동은 app NavGraph가 처리)
 */
@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        UiState.Loading -> LoadingIndicator(modifier)
        is UiState.Error -> ErrorView(message = state.message, modifier = modifier)
        is UiState.Success -> HomeContent(
            data = state.data,
            onProductClick = onProductClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun HomeContent(
    data: HomeUiModel,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 루트를 LazyVerticalGrid로 두어 추천 상품을 2열 그리드로, 나머지 섹션은 전체폭으로 배치한다.
    // (전체폭 섹션은 span = { GridItemSpan(maxLineSpan) } 으로 한 줄 전체를 차지)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) { BannerCarousel(data.banners) }

        // 최근 본 상품 기반 개인화 추천 (최근 본 게 있을 때만 노출)
        if (data.categoryRecommendations.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader("최근 본 상품 기반 추천")
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                HorizontalProductRow(data.categoryRecommendations, onProductClick)
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) { SectionHeader("추천 상품") }
        gridItems(data.recommended, key = { it.id }) { product ->
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

        item(span = { GridItemSpan(maxLineSpan) }) { SectionHeader("실시간 랭킹") }
        item(span = { GridItemSpan(maxLineSpan) }) { RankingRow(data.ranking, onProductClick) }

        item(span = { GridItemSpan(maxLineSpan) }) { SectionHeader("최근 본 상품") }
        item(span = { GridItemSpan(maxLineSpan) }) { RecentViewedRow(data.recentViewed, onProductClick) }
    }
}

/** 상단 배너 캐러셀. */
@Composable
private fun BannerCarousel(banners: List<Banner>) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { banners.size })
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
    ) { page ->
        val banner = banners[page]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
        ) {
            NetworkImage(
                imageUrl = banner.imageUrl,
                contentDescription = banner.title,
                modifier = Modifier.fillMaxSize(),
            )
            // 텍스트 가독성용 어두운 스크림
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.28f))
                    .padding(16.dp),
            ) {
                Text(
                    text = banner.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
                Text(
                    text = banner.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
        }
    }
}

/** 섹션 제목. */
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp),
    )
}

/** 랭킹 가로 목록. 순위 숫자 + 썸네일 + 정보. */
@Composable
private fun RankingRow(
    ranking: List<Product>,
    onProductClick: (String) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(ranking, key = { _, p -> p.id }) { index, product ->
            Row(
                modifier = Modifier
                    .width(260.dp)
                    .clickable { onProductClick(product.id) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                NetworkImage(
                    imageUrl = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(width = 56.dp, height = 72.dp)
                        .clip(RoundedCornerShape(6.dp)),
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = product.brand,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    PriceText(price = product.price)
                }
            }
        }
    }
}

/** 최근 본 상품 행. 비어있으면 안내 문구를 보여준다. */
@Composable
private fun RecentViewedRow(
    recentViewed: List<Product>,
    onProductClick: (String) -> Unit,
) {
    if (recentViewed.isEmpty()) {
        Text(
            text = "최근 본 상품이 없어요. 상품을 둘러보세요!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )
        return
    }
    HorizontalProductRow(recentViewed, onProductClick)
}

/** 상품을 가로 스크롤로 나열하는 공용 행(추천/최근 본 상품 등에 재사용). */
@Composable
private fun HorizontalProductRow(
    products: List<Product>,
    onProductClick: (String) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(products, key = { it.id }) { product ->
            ProductCard(
                brand = product.brand,
                name = product.name,
                price = product.price,
                imageUrl = product.imageUrl,
                rating = product.rating,
                reviewCount = product.reviewCount,
                onClick = { onProductClick(product.id) },
                modifier = Modifier.width(140.dp),
            )
        }
    }
}

package kr.hnu.ice.finalproject.feature.productdetail

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.common.UiState
import kr.hnu.ice.finalproject.core.common.util.PriceFormatter
import kr.hnu.ice.finalproject.core.designsystem.component.EmptyView
import kr.hnu.ice.finalproject.core.designsystem.component.ErrorView
import kr.hnu.ice.finalproject.core.designsystem.component.LoadingIndicator
import kr.hnu.ice.finalproject.core.designsystem.component.NetworkImage
import kr.hnu.ice.finalproject.core.model.Product

/**
 * 상품 비교 화면. 비교함에 담긴 2~3개 상품의 가격/평점/사이즈 등을 나란히 비교한다.
 *
 * @param onProductClick 상품 열 탭 시 상세로 이동
 * @param onBack 뒤로가기
 */
@Composable
fun ComparisonScreen(
    onProductClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ComparisonViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        // 상단바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
            }
            Text(
                text = "상품 비교",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            if (uiState is UiState.Success && (uiState as UiState.Success).data.isNotEmpty()) {
                TextButton(onClick = viewModel::clearAll) { Text("전체 삭제") }
            }
        }

        when (val state = uiState) {
            UiState.Loading -> LoadingIndicator()
            is UiState.Error -> ErrorView(message = state.message)
            is UiState.Success -> {
                val products = state.data
                if (products.size < 2) {
                    EmptyView(
                        message = "비교할 상품을 2개 이상 담아주세요.\n상품 상세에서 '비교 담기'를 눌러보세요.",
                    )
                } else {
                    CompareTable(
                        products = products,
                        onRemove = viewModel::remove,
                        onProductClick = onProductClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun CompareTable(
    products: List<Product>,
    onRemove: (String) -> Unit,
    onProductClick: (String) -> Unit,
) {
    // 강조 기준: 최저가 / 최고 평점
    val minPrice = products.minOf { it.price }
    val maxRating = products.maxOf { it.rating }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
    ) {
        // 상품 헤더(이미지 + 브랜드/이름 + 제거)
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer72()
            products.forEach { product ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .clickable { onProductClick(product.id) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        NetworkImage(
                            imageUrl = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                        )
                        IconButton(
                            onClick = { onRemove(product.id) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp),
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "비교에서 제거")
                        }
                    }
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
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(vertical = 8.dp),
        )

        AttributeRow("가격", products, highlight = { it.price == minPrice }) {
            PriceFormatter.format(it.price) + if (it.price == minPrice) " · 최저" else ""
        }
        AttributeRow("평점", products, highlight = { it.rating == maxRating }) {
            "★ ${"%.1f".format(it.rating)}" + if (it.rating == maxRating) " · 최고" else ""
        }
        AttributeRow("리뷰", products) { "${it.reviewCount}개" }
        AttributeRow("사이즈", products) {
            it.options.map { o -> o.size }.distinct().joinToString(", ")
        }
        AttributeRow("색상", products) {
            it.options.map { o -> o.color }.distinct().joinToString(", ")
        }
        AttributeRow("카테고리", products) { it.category.name }
    }
}

/** 비교 속성 한 줄: 왼쪽 라벨 + 상품별 값. highlight면 포인트 컬러+볼드. */
@Composable
private fun AttributeRow(
    label: String,
    products: List<Product>,
    highlight: (Product) -> Boolean = { false },
    valueOf: (Product) -> String,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.width(72.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            products.forEach { product ->
                val isHighlight = highlight(product)
                Text(
                    text = valueOf(product),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/** 헤더 정렬용 라벨 폭 스페이서. */
@Composable
private fun Spacer72() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(72.dp))
}
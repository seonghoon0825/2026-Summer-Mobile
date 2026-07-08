package kr.hnu.ice.finalproject.feature.productdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.common.UiState
import kr.hnu.ice.finalproject.core.designsystem.component.AppButton
import kr.hnu.ice.finalproject.core.designsystem.component.AppButtonStyle
import kr.hnu.ice.finalproject.core.designsystem.component.ErrorView
import kr.hnu.ice.finalproject.core.designsystem.component.LoadingIndicator
import kr.hnu.ice.finalproject.core.designsystem.component.NetworkImage
import kr.hnu.ice.finalproject.core.designsystem.component.PriceText
import kr.hnu.ice.finalproject.core.designsystem.component.RatingBar
import kr.hnu.ice.finalproject.core.model.Product
import kr.hnu.ice.finalproject.core.model.Review

/**
 * 상품 상세 화면.
 * 이미지 갤러리 + 정보 + 설명 + 리뷰 요약, 하단에 찜/장바구니 담기 액션바.
 *
 * @param onBack 뒤로가기(app이 pop 처리)
 */
@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    onNavigateToCompare: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val compareState by viewModel.compareState.collectAsStateWithLifecycle()
    val model = (uiState as? UiState.Success)?.data
    var showOptionSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // 일회성 이벤트(스낵바)
    LaunchedEffect(Unit) {
        viewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            DetailTopBar(
                isWished = model?.isWished == true,
                onBack = onBack,
                onToggleWish = viewModel::toggleWish,
                showWish = model != null,
            )
        },
        bottomBar = {
            if (model != null) {
                BottomActionBar(
                    isInCompare = compareState.isInCompare,
                    compareCount = compareState.count,
                    onToggleCompare = viewModel::toggleCompare,
                    onNavigateToCompare = onNavigateToCompare,
                    onAddClick = { showOptionSheet = true },
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when (val state = uiState) {
            UiState.Loading -> LoadingIndicator(Modifier.padding(padding))
            is UiState.Error -> ErrorView(message = state.message, modifier = Modifier.padding(padding))
            is UiState.Success -> DetailContent(
                data = state.data,
                modifier = Modifier.padding(padding),
            )
        }
    }

    if (showOptionSheet && model != null) {
        OptionBottomSheet(
            product = model.product,
            onAddToCart = { option, quantity ->
                // 담기 → CartRepository 저장. 완료 스낵바는 viewModel.events로 표시된다.
                viewModel.addToCart(option, quantity)
                showOptionSheet = false
            },
            onDismiss = { showOptionSheet = false },
        )
    }
}

@Composable
private fun DetailTopBar(
    isWished: Boolean,
    onBack: () -> Unit,
    onToggleWish: () -> Unit,
    showWish: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
        }
        if (showWish) {
            IconButton(onClick = onToggleWish) {
                Icon(
                    imageVector = if (isWished) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "찜하기",
                    tint = if (isWished) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    isInCompare: Boolean,
    compareCount: Int,
    onToggleCompare: () -> Unit,
    onNavigateToCompare: () -> Unit,
    onAddClick: () -> Unit,
) {
    Surface(shadowElevation = 8.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 비교함에 2개 이상 담기면 '비교하기' 진입 배너 노출
            if (compareCount >= 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCompare() }
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "비교함 ${compareCount}개",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "비교하기 ›",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppButton(
                    text = if (isInCompare) "비교 담김" else "비교 담기",
                    onClick = onToggleCompare,
                    modifier = Modifier.weight(1f),
                    style = AppButtonStyle.Secondary,
                )
                AppButton(
                    text = "장바구니 담기",
                    onClick = onAddClick,
                    modifier = Modifier.weight(2f),
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    data: ProductDetailUiModel,
    modifier: Modifier = Modifier,
) {
    val product = data.product
    // 단일 이미지를 갤러리처럼 보이도록 같은 사진의 크롭 3장 구성
    val images = remember(product.id) {
        val base = product.imageUrl.substringBefore("?")
        listOf(
            product.imageUrl,
            "$base?auto=format&fit=crop&crop=top&w=600&h=700&q=80",
            "$base?auto=format&fit=crop&crop=bottom&w=600&h=700&q=80",
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // 이미지 갤러리(가로 스와이프)
        ImageGallery(images = images, contentDescription = product.name)

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text = product.brand, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
            RatingBar(rating = product.rating, showValue = true, reviewCount = product.reviewCount)
            PriceText(price = product.price, style = MaterialTheme.typography.titleLarge)
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // 상품 설명
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "상품 정보", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // 리뷰 요약
        ReviewSummary(reviews = data.reviews)
    }
}

@Composable
private fun ImageGallery(images: List<String>, contentDescription: String) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    ) { page ->
        NetworkImage(
            imageUrl = images[page],
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ReviewSummary(reviews: List<Review>) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "리뷰 ${reviews.size}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        if (reviews.isEmpty()) {
            Text(
                text = "아직 리뷰가 없어요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@Column
        }

        // 포토 리뷰 썸네일
        val photoReviews = reviews.filter { it.imageUrl != null }
        if (photoReviews.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(photoReviews, key = { it.id }) { review ->
                    NetworkImage(
                        imageUrl = review.imageUrl,
                        contentDescription = "포토리뷰",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    )
                }
            }
        }

        // 텍스트 리뷰 몇 개
        reviews.take(3).forEach { review ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = review.author, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    RatingBar(rating = review.rating.toDouble(), starSize = 12.dp)
                }
                Text(
                    text = review.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
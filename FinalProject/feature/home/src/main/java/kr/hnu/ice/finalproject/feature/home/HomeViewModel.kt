package kr.hnu.ice.finalproject.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kr.hnu.ice.finalproject.core.common.UiState
import kr.hnu.ice.finalproject.core.data.repository.ProductRepository
import kr.hnu.ice.finalproject.core.model.Product
import javax.inject.Inject

/** 홈 상단 배너. (Mock: 이미지 URL + 문구) */
data class Banner(
    val imageUrl: String,
    val title: String,
    val subtitle: String,
)

/** 홈 화면에 표시할 데이터 묶음. */
data class HomeUiModel(
    val banners: List<Banner>,
    val recommended: List<Product>,
    val ranking: List<Product>,
    val recentViewed: List<Product>,
    // 최근 본 상품과 같은 카테고리의 추천 상품(개인화 추천)
    val categoryRecommendations: List<Product>,
)

/**
 * 홈 ViewModel. ProductRepository에서 상품을 가져와 UiState로 노출한다.
 * (데이터 출처가 Mock인지 서버인지는 알지 못한다 — Repository가 숨긴다)
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    productRepository: ProductRepository,
) : ViewModel() {

    val uiState: StateFlow<UiState<HomeUiModel>> = combine(
        productRepository.getProducts(),
        productRepository.getRecentViewedProducts(),
    ) { products, recentViewed ->
        val model = HomeUiModel(
            banners = DEFAULT_BANNERS,
            recommended = products,
            // 랭킹: 리뷰 수 기준 상위 10개
            ranking = products.sortedByDescending { it.reviewCount }.take(RANKING_SIZE),
            recentViewed = recentViewed,
            categoryRecommendations = buildCategoryRecommendations(products, recentViewed),
        )
        UiState.Success(model) as UiState<HomeUiModel>
    }
        .catch { e -> emit(UiState.Error(e.message ?: "상품을 불러오지 못했어요.", e)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    /**
     * 최근 본 상품과 같은 카테고리의 추천 상품을 만든다.
     * 최근 본 순서대로 카테고리 우선순위를 두고, 이미 본 상품은 제외한다.
     */
    private fun buildCategoryRecommendations(
        products: List<Product>,
        recentViewed: List<Product>,
    ): List<Product> {
        if (recentViewed.isEmpty()) return emptyList()
        val recentIds = recentViewed.map { it.id }.toSet()
        val orderedCategoryIds = recentViewed.map { it.category.id }.distinct()
        return orderedCategoryIds
            .flatMap { categoryId ->
                products.filter { it.category.id == categoryId && it.id !in recentIds }
            }
            .distinctBy { it.id }
            .take(RECOMMENDATION_SIZE)
    }

    private companion object {
        const val RANKING_SIZE = 10
        const val RECOMMENDATION_SIZE = 10

        // Mock 배너 (무료 이미지)
        val DEFAULT_BANNERS = listOf(
            Banner("https://picsum.photos/seed/banner1/800/400", "여름 시즌 오프", "인기 브랜드 최대 50%"),
            Banner("https://picsum.photos/seed/banner2/800/400", "신상 입고", "이번 주 뉴 아이템"),
            Banner("https://picsum.photos/seed/banner3/800/400", "무신사 추천", "지금 뜨는 코디"),
        )
    }
}
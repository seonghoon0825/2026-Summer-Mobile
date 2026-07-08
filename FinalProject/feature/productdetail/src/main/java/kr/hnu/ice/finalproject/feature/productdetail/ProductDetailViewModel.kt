package kr.hnu.ice.finalproject.feature.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.hnu.ice.finalproject.core.common.UiState
import kr.hnu.ice.finalproject.core.data.repository.CartRepository
import kr.hnu.ice.finalproject.core.data.repository.CompareRepository
import kr.hnu.ice.finalproject.core.data.repository.ProductRepository
import kr.hnu.ice.finalproject.core.data.repository.ReviewRepository
import kr.hnu.ice.finalproject.core.data.repository.WishRepository
import kr.hnu.ice.finalproject.core.model.Product
import kr.hnu.ice.finalproject.core.model.ProductOption
import kr.hnu.ice.finalproject.core.model.Review
import javax.inject.Inject

/** 상품 상세 화면 데이터. */
data class ProductDetailUiModel(
    val product: Product,
    val reviews: List<Review>,
    val isWished: Boolean,
)

/** 비교함 상태(이 상품이 담겼는지 + 현재 담긴 개수). */
data class CompareState(
    val isInCompare: Boolean = false,
    val count: Int = 0,
)

/**
 * 상품 상세 ViewModel.
 * - productId(route 인자)로 상세/리뷰/찜상태 로드
 * - 장바구니 담기(CartRepository), 찜 토글(WishRepository)
 * - 진입 시 '최근 본 상품' 기록
 */
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    reviewRepository: ReviewRepository,
    private val cartRepository: CartRepository,
    private val wishRepository: WishRepository,
    private val compareRepository: CompareRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val productId: String = savedStateHandle.get<String>(ARG_PRODUCT_ID).orEmpty()

    // 일회성 이벤트(스낵바 등)
    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events.asSharedFlow()

    // 비교함 상태(담김 여부 + 담긴 개수)
    val compareState: StateFlow<CompareState> = compareRepository.observeSelectedIds()
        .map { ids -> CompareState(isInCompare = productId in ids, count = ids.size) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CompareState(),
        )

    val uiState: StateFlow<UiState<ProductDetailUiModel>> = combine(
        flow { emit(productRepository.getProductById(productId)) },
        reviewRepository.getReviewsByProduct(productId),
        wishRepository.isWished(productId),
    ) { product, reviews, wished ->
        val result: UiState<ProductDetailUiModel> = if (product == null) {
            UiState.Error("상품을 찾을 수 없어요.")
        } else {
            UiState.Success(ProductDetailUiModel(product, reviews, wished))
        }
        result
    }
        .catch { e -> emit(UiState.Error(e.message ?: "상품을 불러오지 못했어요.", e)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    init {
        // 최근 본 상품으로 기록 (홈의 '최근 본 상품' 섹션에 반영됨)
        viewModelScope.launch { productRepository.addRecentViewed(productId) }
    }

    /** 찜 토글. isWished Flow가 갱신되어 UI에 자동 반영된다. */
    fun toggleWish() {
        viewModelScope.launch { wishRepository.toggleWish(productId) }
    }

    /** 비교함 담기/빼기 토글. 최대 개수를 넘으면 안내 메시지를 보낸다. */
    fun toggleCompare() {
        viewModelScope.launch {
            val current = compareRepository.observeSelectedIds().first()
            if (productId !in current && current.size >= CompareRepository.MAX_COMPARE) {
                _events.emit("비교는 최대 ${CompareRepository.MAX_COMPARE}개까지 가능해요")
                return@launch
            }
            val added = compareRepository.toggle(productId)
            _events.emit(if (added) "비교함에 담았어요" else "비교함에서 뺐어요")
        }
    }

    /** 선택한 옵션으로 장바구니에 담는다. CartRepository에 저장되어 장바구니 탭에 반영된다. */
    fun addToCart(option: ProductOption, quantity: Int) {
        val product = (uiState.value as? UiState.Success)?.data?.product ?: return
        viewModelScope.launch {
            cartRepository.addToCart(product, option, quantity)
            _events.emit("장바구니에 담았어요")
        }
    }

    companion object {
        /** app NavGraph의 route 인자 키와 일치해야 한다. */
        const val ARG_PRODUCT_ID = "productId"
    }
}
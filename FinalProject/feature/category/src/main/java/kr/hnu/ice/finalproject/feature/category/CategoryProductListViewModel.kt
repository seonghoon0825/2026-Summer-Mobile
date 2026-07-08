package kr.hnu.ice.finalproject.feature.category

import androidx.lifecycle.SavedStateHandle
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

/** 선택한 카테고리의 상품 목록 + 카테고리 이름. */
data class CategoryProducts(
    val categoryName: String,
    val products: List<Product>,
)

/**
 * 카테고리별 상품 목록 ViewModel.
 * app이 route 인자로 넘긴 categoryId를 SavedStateHandle로 받아 해당 카테고리 상품만 로드한다.
 */
@HiltViewModel
class CategoryProductListViewModel @Inject constructor(
    productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val categoryId: String = savedStateHandle.get<String>(ARG_CATEGORY_ID).orEmpty()

    val uiState: StateFlow<UiState<CategoryProducts>> = combine(
        productRepository.getCategories(),
        productRepository.getProductsByCategory(categoryId),
    ) { categories, products ->
        val name = categories.firstOrNull { it.id == categoryId }?.name ?: "상품"
        UiState.Success(CategoryProducts(categoryName = name, products = products)) as UiState<CategoryProducts>
    }
        .catch { e -> emit(UiState.Error(e.message ?: "상품을 불러오지 못했어요.", e)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    companion object {
        /** app NavGraph의 route 인자 키와 반드시 일치해야 한다. */
        const val ARG_CATEGORY_ID = "categoryId"
    }
}
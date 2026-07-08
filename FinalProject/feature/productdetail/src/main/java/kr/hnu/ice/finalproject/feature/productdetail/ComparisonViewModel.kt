package kr.hnu.ice.finalproject.feature.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.hnu.ice.finalproject.core.common.UiState
import kr.hnu.ice.finalproject.core.data.repository.CompareRepository
import kr.hnu.ice.finalproject.core.data.repository.ProductRepository
import kr.hnu.ice.finalproject.core.model.Product
import javax.inject.Inject

/**
 * 상품 비교 ViewModel. 비교함에 담긴 상품들을 로드해 나란히 비교할 수 있게 노출한다.
 */
@HiltViewModel
class ComparisonViewModel @Inject constructor(
    private val compareRepository: CompareRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {

    val uiState: StateFlow<UiState<List<Product>>> = compareRepository.observeSelectedIds()
        .map { ids -> ids.mapNotNull { productRepository.getProductById(it) } }
        .map { UiState.Success(it) as UiState<List<Product>> }
        .catch { e -> emit(UiState.Error(e.message ?: "비교 상품을 불러오지 못했어요.", e)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    fun remove(productId: String) {
        viewModelScope.launch { compareRepository.remove(productId) }
    }

    fun clearAll() {
        viewModelScope.launch { compareRepository.clear() }
    }
}
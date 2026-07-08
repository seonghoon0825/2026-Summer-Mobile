package kr.hnu.ice.finalproject.feature.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kr.hnu.ice.finalproject.core.common.UiState
import kr.hnu.ice.finalproject.core.data.repository.ProductRepository
import kr.hnu.ice.finalproject.core.model.Category
import javax.inject.Inject

/** 카테고리 목록 화면용 ViewModel. Repository에서 카테고리 목록을 로드한다. */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    productRepository: ProductRepository,
) : ViewModel() {

    val uiState: StateFlow<UiState<List<Category>>> = productRepository.getCategories()
        .map { UiState.Success(it) as UiState<List<Category>> }
        .catch { e -> emit(UiState.Error(e.message ?: "카테고리를 불러오지 못했어요.", e)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )
}
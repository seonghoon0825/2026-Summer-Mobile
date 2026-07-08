package kr.hnu.ice.finalproject.feature.mypage

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
import kr.hnu.ice.finalproject.core.data.repository.WishRepository
import kr.hnu.ice.finalproject.core.model.WishItem
import javax.inject.Inject

/** 찜 목록. WishRepository의 Flow를 구독한다. */
@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishRepository: WishRepository,
) : ViewModel() {

    val uiState: StateFlow<UiState<List<WishItem>>> = wishRepository.getWishItems()
        .map { UiState.Success(it) as UiState<List<WishItem>> }
        .catch { e -> emit(UiState.Error(e.message ?: "찜 목록을 불러오지 못했어요.", e)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    fun removeWish(productId: String) {
        viewModelScope.launch { wishRepository.removeWish(productId) }
    }
}
package kr.hnu.ice.finalproject.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.hnu.ice.finalproject.core.data.repository.ProductRepository
import kr.hnu.ice.finalproject.core.data.repository.SearchHistoryRepository
import kr.hnu.ice.finalproject.core.model.Product
import javax.inject.Inject

/** 검색 화면 상태. */
data class SearchUiState(
    val query: String = "",
    val recentSearches: List<String> = emptyList(),
    val results: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
    val filter: SearchFilter = SearchFilter(),
    val sort: SortOption = SortOption.POPULAR,
    // 필터 시트에 노출할 선택지(현재 검색 결과에서 추출)
    val availableBrands: List<String> = emptyList(),
    val availableSizes: List<String> = emptyList(),
)

/**
 * 검색 ViewModel.
 * - 검색어로 ProductRepository 조회
 * - 최근 검색어는 SearchHistoryRepository(Room)에 저장/조회
 * - 필터/정렬은 조회된 원본 결과에 메모리에서 적용
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // 필터/정렬 적용 전 원본 검색 결과
    private var rawResults: List<Product> = emptyList()

    init {
        // 최근 검색어 구독
        viewModelScope.launch {
            searchHistoryRepository.getRecentSearches().collect { recents ->
                _uiState.update { it.copy(recentSearches = recents) }
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    /** 검색 실행: 최근 검색어 저장 + 결과 로드. */
    fun search() {
        val query = _uiState.value.query.trim()
        if (query.isBlank()) return
        viewModelScope.launch {
            searchHistoryRepository.addSearch(query)
            _uiState.update { it.copy(isLoading = true, hasSearched = true) }
            val results = productRepository.searchProducts(query).first()
            rawResults = results
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    availableBrands = results.map { it.brand }.distinct().sorted(),
                    availableSizes = results.flatMap { p -> p.options.map { it.size } }.distinct().sorted(),
                    results = applyFilterSort(results, state.filter, state.sort),
                )
            }
        }
    }

    /** 최근 검색어 탭 → 그 검색어로 즉시 검색. */
    fun onRecentSearchClick(keyword: String) {
        _uiState.update { it.copy(query = keyword) }
        search()
    }

    fun removeRecentSearch(keyword: String) {
        viewModelScope.launch { searchHistoryRepository.removeSearch(keyword) }
    }

    fun clearRecentSearches() {
        viewModelScope.launch { searchHistoryRepository.clearSearches() }
    }

    /** 필터/정렬을 함께 적용(바텀시트의 '적용'에서 호출). */
    fun applyFilterAndSort(filter: SearchFilter, sort: SortOption) {
        _uiState.update { state ->
            state.copy(
                filter = filter,
                sort = sort,
                results = applyFilterSort(rawResults, filter, sort),
            )
        }
    }

    private fun applyFilterSort(
        list: List<Product>,
        filter: SearchFilter,
        sort: SortOption,
    ): List<Product> {
        var result = list
        if (filter.brands.isNotEmpty()) {
            result = result.filter { it.brand in filter.brands }
        }
        if (filter.priceRange != PriceRange.ALL) {
            result = result.filter { it.price in filter.priceRange.min..filter.priceRange.max }
        }
        if (filter.sizes.isNotEmpty()) {
            result = result.filter { product -> product.options.any { it.size in filter.sizes } }
        }
        return when (sort) {
            SortOption.POPULAR -> result.sortedByDescending { it.reviewCount }
            SortOption.PRICE_LOW -> result.sortedBy { it.price }
            SortOption.PRICE_HIGH -> result.sortedByDescending { it.price }
            // 상품에 날짜 필드가 없어, id(p1..p24)의 숫자를 최신 기준으로 사용
            SortOption.LATEST -> result.sortedByDescending { it.id.removePrefix("p").toIntOrNull() ?: 0 }
        }
    }
}
package kr.hnu.ice.finalproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kr.hnu.ice.finalproject.core.data.repository.CartRepository
import kr.hnu.ice.finalproject.core.data.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * 앱 시작 시 로그인 여부를 판단한다(자동 로그인).
 * DataStore의 로그인 상태를 읽어 시작 화면(로그인 vs 홈)을 결정한다.
 */
sealed interface AuthUiState {
    /** DataStore를 아직 읽는 중 — 스플래시/로딩 표시 */
    data object Loading : AuthUiState

    /** 로그인 되어 있음 → 홈에서 시작 */
    data object Authenticated : AuthUiState

    /** 비로그인 → 로그인 화면에서 시작 */
    data object Unauthenticated : AuthUiState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    cartRepository: CartRepository,
) : ViewModel() {

    val authState: StateFlow<AuthUiState> = userPreferencesRepository.userData
        .map { if (it.isLoggedIn) AuthUiState.Authenticated else AuthUiState.Unauthenticated }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthUiState.Loading,
        )

    // 하단 탭 장바구니 뱃지용 담긴 상품 개수. CartRepository를 구독해 실시간 갱신.
    val cartCount: StateFlow<Int> = cartRepository.getCartItems()
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0,
        )

    // 다크모드 설정(DataStore). AppTheme에 전달되어 앱 전체에 적용된다.
    val darkTheme: StateFlow<Boolean> = userPreferencesRepository.userData
        .map { it.darkTheme }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
}
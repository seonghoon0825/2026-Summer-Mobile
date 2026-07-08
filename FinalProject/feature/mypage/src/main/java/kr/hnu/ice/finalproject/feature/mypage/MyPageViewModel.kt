package kr.hnu.ice.finalproject.feature.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kr.hnu.ice.finalproject.core.data.repository.UserPreferencesRepository
import kr.hnu.ice.finalproject.core.model.User
import javax.inject.Inject

/** 마이페이지 상단 내 정보. */
@HiltViewModel
class MyPageViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val user: StateFlow<User?> = userPreferencesRepository.userData
        .map { it.user }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )
}
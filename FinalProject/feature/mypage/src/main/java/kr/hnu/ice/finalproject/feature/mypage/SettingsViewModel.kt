package kr.hnu.ice.finalproject.feature.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.hnu.ice.finalproject.core.data.repository.UserPreferencesRepository
import kr.hnu.ice.finalproject.core.model.FontSizeOption
import javax.inject.Inject

/** 설정: 다크모드 + 접근성(글자 크기/고대비) 토글(DataStore 연동) + 로그아웃. */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val darkTheme: StateFlow<Boolean> = userPreferencesRepository.userData
        .map { it.darkTheme }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val fontSize: StateFlow<FontSizeOption> = userPreferencesRepository.userData
        .map { it.fontSize }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FontSizeOption.NORMAL,
        )

    val highContrast: StateFlow<Boolean> = userPreferencesRepository.userData
        .map { it.highContrast }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setDarkTheme(enabled) }
    }

    fun setFontSize(option: FontSizeOption) {
        viewModelScope.launch { userPreferencesRepository.setFontSize(option) }
    }

    fun setHighContrast(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setHighContrast(enabled) }
    }

    fun logout() {
        viewModelScope.launch { userPreferencesRepository.clearUser() }
    }
}
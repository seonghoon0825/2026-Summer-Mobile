package kr.hnu.ice.finalproject.feature.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.hnu.ice.finalproject.core.data.repository.UserPreferencesRepository
import kr.hnu.ice.finalproject.core.model.User
import javax.inject.Inject

/**
 * 로그인/회원가입 폼 상태.
 * 하나의 ViewModel을 Login/SignUp 화면이 각자의 인스턴스로 사용한다(각 화면 route에 스코프됨).
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val confirmPasswordError: String? = null,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
)

/**
 * 가짜 인증(백엔드 없음): 입력 형식만 맞으면 통과시키고, 유저 정보를 DataStore에 저장한다.
 * 저장은 UserPreferencesRepository가 담당하므로, 이 ViewModel은 출처(DataStore)를 모른다.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, emailError = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, passwordError = null) }
    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, nameError = null) }
    fun onConfirmPasswordChange(value: String) =
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }

    /** 로그인: 이메일/비밀번호 형식 검증 후 통과하면 유저 저장. */
    fun login() {
        val state = _uiState.value
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)
        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }
        // 로그인 시 이름은 이메일 아이디 부분으로 대체(가짜 인증)
        val user = User(
            id = state.email,
            name = state.email.substringBefore("@"),
            email = state.email,
        )
        submit(user)
    }

    /** 회원가입: 이름/이메일/비밀번호/비밀번호 확인 검증 후 통과하면 유저 저장. */
    fun signUp() {
        val state = _uiState.value
        val nameError = if (state.name.isBlank()) "이름을 입력해 주세요." else null
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)
        val confirmError = when {
            state.confirmPassword.isBlank() -> "비밀번호를 한 번 더 입력해 주세요."
            state.confirmPassword != state.password -> "비밀번호가 일치하지 않습니다."
            else -> null
        }
        if (nameError != null || emailError != null || passwordError != null || confirmError != null) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmError,
                )
            }
            return
        }
        val user = User(id = state.email, name = state.name, email = state.email)
        submit(user)
    }

    /** 유저를 저장하고 성공 상태로 전환한다. */
    private fun submit(user: User) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            userPreferencesRepository.setUser(user)
            _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
        }
    }

    private fun validateEmail(email: String): String? = when {
        email.isBlank() -> "이메일을 입력해 주세요."
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "올바른 이메일 형식이 아닙니다."
        else -> null
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> "비밀번호를 입력해 주세요."
        password.length < MIN_PASSWORD_LENGTH -> "비밀번호는 최소 ${MIN_PASSWORD_LENGTH}자 이상이어야 합니다."
        else -> null
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}
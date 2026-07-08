package kr.hnu.ice.finalproject.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.designsystem.component.AppButton

/**
 * 회원가입 화면.
 *
 * @param onSignUpSuccess 가입 성공 시 호출(홈 이동은 app NavGraph가 처리)
 * @param onBack 뒤로가기(로그인 화면으로)
 */
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onSignUpSuccess()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "회원가입",
            style = MaterialTheme.typography.titleLarge,
        )

        AuthTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            label = "이름",
            error = state.nameError,
        )
        AuthTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "이메일",
            error = state.emailError,
            keyboardType = KeyboardType.Email,
        )
        AuthTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "비밀번호 (6자 이상)",
            error = state.passwordError,
            isPassword = true,
            keyboardType = KeyboardType.Password,
        )
        AuthTextField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = "비밀번호 확인",
            error = state.confirmPasswordError,
            isPassword = true,
            keyboardType = KeyboardType.Password,
        )

        AppButton(
            text = if (state.isSubmitting) "가입 중..." else "가입하고 시작하기",
            onClick = viewModel::signUp,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSubmitting,
        )

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("이미 계정이 있으신가요? 로그인")
        }
    }
}
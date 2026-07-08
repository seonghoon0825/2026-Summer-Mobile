package kr.hnu.ice.finalproject.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.designsystem.component.AppButton

/**
 * 로그인 화면.
 *
 * @param onLoginSuccess 로그인 성공 시 호출(홈 이동은 app NavGraph가 처리)
 * @param onNavigateToSignUp 회원가입 화면으로 이동
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // 로그인 성공 이벤트 → 화면 이동(한 번만)
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onLoginSuccess()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = "로그인",
            style = MaterialTheme.typography.titleLarge,
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
            label = "비밀번호",
            error = state.passwordError,
            isPassword = true,
            keyboardType = KeyboardType.Password,
        )

        AppButton(
            text = if (state.isSubmitting) "로그인 중..." else "로그인",
            onClick = viewModel::login,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSubmitting,
        )

        TextButton(
            onClick = onNavigateToSignUp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("계정이 없으신가요? 회원가입")
        }
    }
}
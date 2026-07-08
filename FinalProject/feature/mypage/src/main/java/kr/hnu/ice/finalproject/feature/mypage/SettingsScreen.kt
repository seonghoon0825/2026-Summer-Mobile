package kr.hnu.ice.finalproject.feature.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.designsystem.component.AppButton
import kr.hnu.ice.finalproject.core.designsystem.component.AppButtonStyle

/**
 * 설정 화면. 다크모드 토글(DataStore 연동) + 찜 가격 알림 + 로그아웃.
 *
 * @param onSendPriceAlert 찜 상품 가격 인하 알림 보내기(권한/발송은 app이 처리)
 * @param onLoggedOut 로그아웃 완료 후 처리(app이 로그인 화면으로 이동)
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSendPriceAlert: () -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        MyPageTopBar(title = "설정", onBack = onBack)

        // 다크모드 토글
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "다크 모드", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = darkTheme,
                onCheckedChange = viewModel::setDarkTheme,
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // 찜 상품 가격 인하 알림 (데모: 찜 상품에 세일을 적용해 알림 발송)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.padding(end = 12.dp)) {
                Text(text = "찜 상품 가격 인하 알림", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "찜한 상품에 세일을 적용하고 알림을 보냅니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            AppButton(
                text = "알림 보내기",
                onClick = onSendPriceAlert,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(top = 16.dp),
        )

        Column(modifier = Modifier.padding(20.dp)) {
            AppButton(
                text = "로그아웃",
                onClick = {
                    viewModel.logout()
                    onLoggedOut()
                },
                modifier = Modifier.fillMaxWidth(),
                style = AppButtonStyle.Secondary,
            )
        }
    }
}
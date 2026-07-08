package kr.hnu.ice.finalproject.feature.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.designsystem.component.AppButton
import kr.hnu.ice.finalproject.core.designsystem.component.AppButtonStyle
import kr.hnu.ice.finalproject.core.model.FontSizeOption

// 글자 크기 옵션 라벨(FontSizeOption enum 순서와 일치: SMALL/NORMAL/LARGE/EXTRA_LARGE)
private val FONT_SIZE_LABELS = mapOf(
    FontSizeOption.SMALL to "작게",
    FontSizeOption.NORMAL to "보통",
    FontSizeOption.LARGE to "크게",
    FontSizeOption.EXTRA_LARGE to "아주 크게",
)

/**
 * 설정 화면. 다크모드 + 접근성(글자 크기/고대비) + 찜 가격 알림 + 로그아웃.
 * 테마/접근성 설정은 DataStore에 저장되어 앱 전체(AppTheme)에 즉시 반영된다.
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
    val fontSize by viewModel.fontSize.collectAsStateWithLifecycle()
    val highContrast by viewModel.highContrast.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        MyPageTopBar(title = "설정", onBack = onBack)

        // ---- 화면 ----
        SectionHeader("화면")
        ToggleRow(
            title = "다크 모드",
            checked = darkTheme,
            onCheckedChange = viewModel::setDarkTheme,
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // ---- 접근성 ----
        SectionHeader("접근성")

        // 글자 크기
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(text = "글자 크기", style = MaterialTheme.typography.bodyLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FontSizeOption.entries.forEach { option ->
                    FilterChip(
                        selected = fontSize == option,
                        onClick = { viewModel.setFontSize(option) },
                        label = { Text(FONT_SIZE_LABELS.getValue(option)) },
                    )
                }
            }
            // 현재 글자 크기가 본문에 어떻게 보이는지 미리보기
            Text(
                text = "미리보기: 오늘도 좋은 하루 되세요 :)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // 고대비
        ToggleRow(
            title = "고대비 모드",
            description = "글자와 배경의 명암 대비를 강화합니다.",
            checked = highContrast,
            onCheckedChange = viewModel::setHighContrast,
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // ---- 알림 ----
        SectionHeader("알림")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "찜 상품 가격 인하 알림", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "찜한 상품에 세일을 적용하고 알림을 보냅니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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

/** 섹션 구분 헤더. 스크린리더가 제목으로 인식하도록 heading 시맨틱을 부여한다. */
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp)
            .semantics { heading() },
    )
}

/**
 * 스위치 설정 행. 제목(+선택 설명)과 오른쪽 스위치로 구성한다.
 * 접근성 최소 터치 영역(48dp) 확보를 위해 행 높이를 최소 56dp로 유지한다.
 */
@Composable
private fun ToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
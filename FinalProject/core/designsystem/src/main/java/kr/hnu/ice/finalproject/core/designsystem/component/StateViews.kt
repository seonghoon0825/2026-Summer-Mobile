package kr.hnu.ice.finalproject.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.hnu.ice.finalproject.core.designsystem.theme.AppTheme

/** 로딩 인디케이터. 주어진 영역 중앙에 스피너를 표시한다. */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

/**
 * 에러 화면. 메시지와(선택) 재시도 버튼을 보여준다.
 *
 * @param message 사용자에게 보여줄 에러 메시지
 * @param onRetry null이 아니면 "다시 시도" 버튼을 노출
 */
@Composable
fun ErrorView(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    MessageView(
        icon = Icons.Filled.WarningAmber,
        message = message,
        modifier = modifier,
        actionText = if (onRetry != null) "다시 시도" else null,
        onAction = onRetry,
    )
}

/**
 * 빈 상태 화면. (검색 결과 없음, 장바구니 비어있음 등)
 *
 * @param message 안내 메시지
 * @param actionText null이 아니면 액션 버튼을 노출
 * @param onAction 액션 버튼 콜백
 */
@Composable
fun EmptyView(
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    MessageView(
        icon = Icons.Filled.Inbox,
        message = message,
        modifier = modifier,
        actionText = actionText,
        onAction = onAction,
    )
}

/** ErrorView/EmptyView 공용 레이아웃: 아이콘 + 메시지 + (선택)버튼을 중앙 정렬. */
@Composable
private fun MessageView(
    icon: ImageVector,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp),
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (actionText != null && onAction != null) {
                AppButton(text = actionText, onClick = onAction)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 240)
@Composable
private fun LoadingIndicatorPreview() {
    AppTheme { LoadingIndicator() }
}

@Preview(showBackground = true, heightDp = 320)
@Composable
private fun ErrorViewPreview() {
    AppTheme { ErrorView(message = "상품을 불러오지 못했어요.", onRetry = {}) }
}

@Preview(showBackground = true, heightDp = 320)
@Composable
private fun EmptyViewPreview() {
    AppTheme { EmptyView(message = "장바구니가 비어 있어요.", actionText = "쇼핑하러 가기", onAction = {}) }
}
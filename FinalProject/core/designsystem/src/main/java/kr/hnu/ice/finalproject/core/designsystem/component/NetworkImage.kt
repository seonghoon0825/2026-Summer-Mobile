package kr.hnu.ice.finalproject.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import kr.hnu.ice.finalproject.core.designsystem.theme.AppTheme

/**
 * Coil3 기반 네트워크 이미지 로더.
 * 로딩 중에는 스피너를, 실패 시에는 깨진 이미지 아이콘 placeholder를 보여준다.
 *
 * @param imageUrl 이미지 URL (null/빈 문자열이면 에러 placeholder)
 * @param contentDescription 접근성 설명
 * @param contentScale 스케일 방식 (기본: Crop)
 */
@Composable
fun NetworkImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        loading = { PlaceholderBox { CircularProgressIndicator(strokeWidth = 2.dp) } },
        error = {
            PlaceholderBox {
                Icon(
                    imageVector = Icons.Filled.BrokenImage,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp),
                )
            }
        },
    )
}

/** 로딩/에러 시 이미지 영역을 채우는 회색 배경 박스. */
@Composable
private fun PlaceholderBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun NetworkImagePreview() {
    AppTheme {
        // Preview에서는 네트워크가 없으므로 에러 placeholder가 표시된다.
        NetworkImage(
            imageUrl = "https://example.com/sample.jpg",
            contentDescription = "샘플 이미지",
            modifier = Modifier.size(160.dp),
        )
    }
}
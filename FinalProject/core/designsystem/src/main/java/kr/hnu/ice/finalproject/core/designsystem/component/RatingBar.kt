package kr.hnu.ice.finalproject.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kr.hnu.ice.finalproject.core.designsystem.theme.AppTheme

private val StarAmber = Color(0xFFFFC107)

/**
 * 별점 표시 컴포넌트. 0.5 단위로 반올림해 꽉 찬 별 / 반 별 / 빈 별로 그린다.
 *
 * @param rating 평점 (0.0 ~ 5.0)
 * @param starSize 별 아이콘 크기
 * @param showValue 숫자 평점(예: "4.5")을 별 뒤에 함께 표시할지 여부
 * @param reviewCount null이 아니면 "(123)" 형태로 리뷰 수를 함께 표시
 */
@Composable
fun RatingBar(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: androidx.compose.ui.unit.Dp = 16.dp,
    showValue: Boolean = false,
    reviewCount: Int? = null,
) {
    // 0.5 단위로 스냅 (예: 4.3 -> 4.5, 4.1 -> 4.0)
    val snapped = (rating * 2).roundToInt() / 2.0
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            val icon: ImageVector = when {
                snapped >= i -> Icons.Filled.Star
                snapped >= i - 0.5 -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Filled.StarBorder
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = StarAmber,
                modifier = Modifier.size(starSize).padding(end = 1.dp),
            )
        }
        if (showValue) {
            Text(
                text = " ${"%.1f".format(rating)}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (reviewCount != null) {
            Text(
                text = " ($reviewCount)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RatingBarPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RatingBar(rating = 4.5, showValue = true, reviewCount = 128)
            RatingBar(rating = 3.0)
            RatingBar(rating = 4.2, showValue = true)
        }
    }
}
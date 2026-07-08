package kr.hnu.ice.finalproject.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.hnu.ice.finalproject.core.common.util.PriceFormatter
import kr.hnu.ice.finalproject.core.designsystem.theme.AppTheme

/**
 * 가격을 강조 표시하는 텍스트. 내부적으로 [PriceFormatter]로 "₩12,900" 형태로 변환한다.
 *
 * @param price 원(KRW) 단위 정수 가격
 * @param style 텍스트 스타일 (기본: titleMedium, Bold 적용)
 * @param emphasize true면 포인트 컬러로 강조(세일가 등)
 */
@Composable
fun PriceText(
    price: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    emphasize: Boolean = false,
) {
    Text(
        text = PriceFormatter.format(price),
        modifier = modifier,
        style = style.copy(fontWeight = FontWeight.Bold),
        color = if (emphasize) AppTheme.pointColor else MaterialTheme.colorScheme.onSurface,
    )
}

@Preview(showBackground = true)
@Composable
private fun PriceTextPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PriceText(price = 12900)
            PriceText(price = 89000, emphasize = true)
        }
    }
}
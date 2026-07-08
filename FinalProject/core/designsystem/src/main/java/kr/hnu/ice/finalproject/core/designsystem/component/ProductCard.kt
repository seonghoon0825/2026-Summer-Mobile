package kr.hnu.ice.finalproject.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.hnu.ice.finalproject.core.designsystem.theme.AppTheme

/**
 * 상품 카드. 목록/그리드에서 상품 하나를 요약해 보여준다.
 * 도메인 모델과 분리하기 위해 원시 파라미터를 받는다.
 *
 * @param brand 브랜드명
 * @param name 상품명
 * @param price 가격(원)
 * @param imageUrl 상품 이미지 URL
 * @param rating 평균 평점 (0.0 ~ 5.0)
 * @param reviewCount 리뷰 수 (null이면 표시 안 함)
 * @param onClick 카드 클릭 콜백
 */
@Composable
fun ProductCard(
    brand: String,
    name: String,
    price: Int,
    imageUrl: String?,
    rating: Double,
    modifier: Modifier = Modifier,
    reviewCount: Int? = null,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // 상품 이미지 (세로형 카드 느낌: 3:4 비율)
        NetworkImage(
            imageUrl = imageUrl,
            contentDescription = name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(RoundedCornerShape(8.dp)),
        )
        // 브랜드
        Text(
            text = brand,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        // 상품명
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        // 가격
        PriceText(price = price)
        // 별점 + 리뷰 수
        RatingBar(rating = rating, starSize = 14.dp, showValue = true, reviewCount = reviewCount)
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable
private fun ProductCardPreview() {
    AppTheme {
        ProductCard(
            brand = "커버낫",
            name = "어센틱 로고 스웨트셔츠 블랙",
            price = 59000,
            imageUrl = "https://example.com/sample.jpg",
            rating = 4.5,
            reviewCount = 128,
            modifier = Modifier.width(180.dp),
        )
    }
}
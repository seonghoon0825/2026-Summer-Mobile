package kr.hnu.ice.finalproject.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.hnu.ice.finalproject.core.designsystem.theme.AppTheme

/** 버튼 스타일. Primary=채워진 버튼, Secondary=외곽선 버튼. */
enum class AppButtonStyle { Primary, Secondary }

/**
 * 앱 공용 버튼.
 *
 * @param text 버튼 라벨
 * @param onClick 클릭 콜백
 * @param style 기본(Primary) / 보조(Secondary)
 * @param enabled 활성화 여부
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(8.dp)
    when (style) {
        AppButtonStyle.Primary -> Button(
            onClick = onClick,
            modifier = modifier.height(52.dp),
            enabled = enabled,
            shape = shape,
        ) {
            Text(text)
        }

        AppButtonStyle.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(52.dp),
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(),
        ) {
            Text(text)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppButtonPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppButton("구매하기", onClick = {}, modifier = Modifier.fillMaxWidth())
            AppButton(
                "장바구니",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                style = AppButtonStyle.Secondary,
            )
            AppButton(
                "품절",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
            )
        }
    }
}

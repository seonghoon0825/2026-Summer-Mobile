package kr.hnu.ice.finalproject.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ---- 라이트/다크 ColorScheme ----
private val LightColorScheme = lightColorScheme(
    primary = Black,
    onPrimary = White,
    secondary = Gray700,
    onSecondary = White,
    background = White,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,
    outline = Gray300,
    outlineVariant = Gray200,
    error = PointRed,
    onError = White,
)

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    secondary = Gray300,
    onSecondary = Black,
    background = DarkBackground,
    onBackground = White,
    surface = DarkSurface,
    onSurface = White,
    surfaceVariant = DarkGray,
    onSurfaceVariant = Gray300,
    outline = Gray700,
    outlineVariant = DarkGray,
    error = PointRed,
    onError = White,
)

// Material3 ColorScheme에는 "포인트 컬러" 슬롯이 없으므로 CompositionLocal로 별도 제공한다.
private val LocalPointColor = staticCompositionLocalOf { PointRed }

/**
 * 앱 전역 테마. Material3 위에 커스텀 포인트 컬러를 얹는다.
 *
 * @param darkTheme 다크 모드 여부 (기본: 시스템 설정)
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val point = if (darkTheme) Color(0xFFFF6B6B) else PointRed

    CompositionLocalProvider(LocalPointColor provides point) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}

/**
 * 디자인 시스템 토큰 접근용 헬퍼. (예: `AppTheme.pointColor`)
 * MaterialTheme가 커버하지 않는 커스텀 토큰을 여기서 노출한다.
 */
object AppTheme {
    /** 가격 강조/세일 등에 쓰는 포인트 컬러. */
    val pointColor: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalPointColor.current
}
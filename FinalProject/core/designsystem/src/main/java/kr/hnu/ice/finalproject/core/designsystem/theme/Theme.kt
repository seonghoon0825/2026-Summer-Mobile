package kr.hnu.ice.finalproject.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

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

// ---- 고대비(접근성) ColorScheme ----
// WCAG 대비를 높이기 위해 텍스트를 순수 흑/백에 가깝게, 경계선을 더 진하게 조정한다.
private val LightHighContrastColorScheme = LightColorScheme.copy(
    onBackground = Black,
    onSurface = Black,
    onSurfaceVariant = Gray900, // 보조 텍스트도 훨씬 진하게(기존 Gray700 → Gray900)
    outline = Gray700,
    outlineVariant = Gray500,
)

private val DarkHighContrastColorScheme = DarkColorScheme.copy(
    background = TrueBlack, // 순수 검정 배경으로 대비 극대화
    surface = HighContrastDarkSurface,
    onSurfaceVariant = White, // 보조 텍스트도 흰색으로(기존 Gray300 → White)
    outline = Gray300,
    outlineVariant = Gray500,
)

// Material3 ColorScheme에는 "포인트 컬러" 슬롯이 없으므로 CompositionLocal로 별도 제공한다.
private val LocalPointColor = staticCompositionLocalOf { PointRed }

/** 타이포그래피 전체 글자 크기/줄간격에 배율을 적용한다(접근성 글자 크기). */
private fun Typography.scaledBy(scale: Float): Typography {
    if (scale == 1f) return this
    fun TextStyle.scaled(): TextStyle = copy(fontSize = fontSize * scale, lineHeight = lineHeight * scale)
    return copy(
        titleLarge = titleLarge.scaled(),
        titleMedium = titleMedium.scaled(),
        bodyLarge = bodyLarge.scaled(),
        bodyMedium = bodyMedium.scaled(),
        labelLarge = labelLarge.scaled(),
        labelSmall = labelSmall.scaled(),
    )
}

/**
 * 앱 전역 테마. Material3 위에 커스텀 포인트 컬러를 얹는다.
 *
 * @param darkTheme 다크 모드 여부 (기본: 시스템 설정)
 * @param fontScale 접근성 글자 크기 배율 (1f = 기본)
 * @param highContrast 고대비(명암 대비 강화) 여부
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontScale: Float = 1f,
    highContrast: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme && highContrast -> DarkHighContrastColorScheme
        darkTheme -> DarkColorScheme
        highContrast -> LightHighContrastColorScheme
        else -> LightColorScheme
    }
    val point = if (darkTheme) Color(0xFFFF6B6B) else PointRed

    CompositionLocalProvider(LocalPointColor provides point) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography.scaledBy(fontScale),
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
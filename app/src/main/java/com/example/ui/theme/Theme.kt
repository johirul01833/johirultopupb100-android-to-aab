package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = DeepViolet,
    onPrimaryContainer = GoldLight,
    secondary = NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = DarkViolet,
    onSecondaryContainer = Color.White,
    tertiary = NeonPink,
    onTertiary = Color.White,
    background = BrandDarkBg,
    onBackground = TextPrimaryLight,
    surface = BrandDarkCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = BrandDarkSurface,
    onSurfaceVariant = TextSecondaryLight,
    outline = BrandDarkCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = DeepViolet,
    onPrimary = Color.White,
    primaryContainer = GoldLight,
    onPrimaryContainer = GoldDark,
    secondary = NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3EDFF),
    onSecondaryContainer = DeepViolet,
    tertiary = NeonMagenta,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = TextPrimaryDark,
    surface = LightCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFFEBEFFA),
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFFE2E8F0)
)

@Composable
fun JohirulTopupTheme(
    darkTheme: Boolean = true, // Default to dark luxury cyberpunk theme matching the brand
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

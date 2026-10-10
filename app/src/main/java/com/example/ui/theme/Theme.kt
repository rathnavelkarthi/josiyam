package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CelestialColorScheme = darkColorScheme(
    primary = CelestialGold,
    onPrimary = CelestialBg,
    primaryContainer = CelestialSurface3,
    onPrimaryContainer = CelestialGoldStrong,
    secondary = CelestialSky,
    onSecondary = CelestialBg,
    secondaryContainer = CelestialSurface2,
    onSecondaryContainer = CelestialSky,
    tertiary = CelestialViolet,
    onTertiary = CelestialBg,
    tertiaryContainer = CelestialSurface2,
    onTertiaryContainer = CelestialViolet,
    background = CelestialBg,
    onBackground = CelestialTextPrimary,
    surface = CelestialSurface,
    onSurface = CelestialTextPrimary,
    surfaceVariant = CelestialSurface2,
    onSurfaceVariant = CelestialTextSecondary,
    outline = CelestialBorder,
    outlineVariant = CelestialBorderStrong,
    error = CelestialNegative,
    onError = CelestialBg
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Consistent luxury celestial dark palette across all modes
    MaterialTheme(
        colorScheme = CelestialColorScheme,
        typography = Typography,
        content = content
    )
}

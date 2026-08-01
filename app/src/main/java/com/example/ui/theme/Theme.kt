package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IosDarkColorScheme = darkColorScheme(
    primary = IosGreen,
    onPrimary = Color.White,
    secondary = IosBlue,
    onSecondary = Color.White,
    tertiary = IosOrange,
    background = IosDarkBackground,
    surface = IosDarkSurface,
    surfaceVariant = IosDarkCard,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = IosDarkTextSecondary
)

private val IosLightColorScheme = lightColorScheme(
    primary = IosGreen,
    onPrimary = Color.White,
    secondary = IosBlue,
    onSecondary = Color.White,
    tertiary = IosOrange,
    background = IosLightBackground,
    surface = IosLightSurface,
    surfaceVariant = IosLightCard,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onSurfaceVariant = IosLightTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to true for iconic iOS Phone Dark Interface
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) IosDarkColorScheme else IosLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


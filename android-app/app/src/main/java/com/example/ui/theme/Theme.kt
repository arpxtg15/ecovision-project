package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AccentLime,
    onPrimary = DeepForestGreen,
    primaryContainer = PrimaryGreen,
    onPrimaryContainer = SoftPaleGreen,
    secondary = NatureGreen,
    onSecondary = LightCreamGreen,
    secondaryContainer = DeepForestGreen,
    onSecondaryContainer = LightGreenBackground,
    tertiary = AccentLime,
    onTertiary = DarkText,
    error = EcoCoralAlert,
    onError = PureWhite,
    background = DeepForestGreen,
    onBackground = LightGreenBackground,
    surface = Color(0xFF183827),
    onSurface = LightCreamGreen,
    surfaceVariant = Color(0xFF204834),
    onSurfaceVariant = SoftPaleGreen,
    outline = NatureGreen,
    outlineVariant = Color(0xFF283B2C)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = LightCreamGreen,
    primaryContainer = LightGreenBackground,
    onPrimaryContainer = DeepForestGreen,
    secondary = NatureGreen,
    onSecondary = LightCreamGreen,
    secondaryContainer = SoftPaleGreen,
    onSecondaryContainer = DeepForestGreen,
    tertiary = NatureGreen,
    onTertiary = LightCreamGreen,
    error = EcoCoralAlert,
    onError = PureWhite,
    background = LightGreenBackground,
    onBackground = DarkText,
    surface = LightCreamGreen,
    onSurface = DarkText,
    surfaceVariant = SoftPaleGreen,
    onSurfaceVariant = DarkText,
    outline = SoftPaleGreen,
    outlineVariant = SoftPaleGreen.copy(alpha = 0.6f)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep false to strictly preserve the exact brand palette
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

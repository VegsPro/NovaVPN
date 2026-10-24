package com.novavpn.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NovaDarkColorScheme = darkColorScheme(
    primary = NovaPrimary,
    onPrimary = Color.White,
    primaryContainer = NovaPrimarySoft,
    onPrimaryContainer = Color.White,
    secondary = NovaAccent,
    onSecondary = Color.Black,
    tertiary = NovaWarning,
    background = NovaBg,
    onBackground = NovaText,
    surface = NovaSurface,
    onSurface = NovaText,
    surfaceVariant = NovaSurfaceHigh,
    onSurfaceVariant = NovaTextMuted,
    outline = NovaBorder,
    error = NovaDanger
)

@Composable
fun NovaVPNTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = NovaDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

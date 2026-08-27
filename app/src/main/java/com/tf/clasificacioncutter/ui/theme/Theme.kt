package com.tf.clasificacioncutter.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CutterPrimary,
    secondary = CutterTextSecondary,
    tertiary = CutterAccent,
    background = CutterPrimary,
    surface = CutterPrimary,
    onPrimary = CutterText,
    onSecondary = CutterText,
    onTertiary = CutterText,
    onBackground = CutterText,
    onSurface = CutterText
)

private val LightColorScheme = lightColorScheme(
    primary = CutterPrimary,
    secondary = CutterTextSecondary,
    tertiary = CutterAccent,
    background = CutterPrimary,
    surface = CutterPrimary,
    onPrimary = CutterText,
    onSecondary = CutterText,
    onTertiary = CutterText,
    onBackground = CutterText,
    onSurface = CutterText
)

@Composable
fun CutterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.reading.ui.theme

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

val PastelBlue = Color(0xFFD6EAF8)
val PastelPink = Color(0xFFF8D7E3)
val PastelPinkDark = Color(0xFFE8B4CB)
val DarkText = Color(0xFF1C1B1F)

private val LightColorScheme = lightColorScheme(
    primary = PastelPink,
    onPrimary = DarkText,
    secondary = PastelBlue,
    tertiary = PastelPink,
    background = PastelBlue,
    onBackground = DarkText,
    surface = PastelBlue,
    onSurface = DarkText
)

private val DarkColorScheme = darkColorScheme(
    primary = PastelPink,
    onPrimary = DarkText,
    secondary = PastelBlue,
    tertiary = PastelPink,
    background = Color(0xFF1B2A33),
    onBackground = Color(0xFFE8F0F5),
    surface = Color(0xFF1B2A33),
    onSurface = Color(0xFFE8F0F5)
)

@Composable
fun ReadingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
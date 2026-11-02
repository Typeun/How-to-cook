package com.typeun.cc.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OrangePrimary = Color(0xFFF57C00)
private val OrangePrimaryDark = Color(0xFFE65100)
private val OrangePrimaryLight = Color(0xFFFFB74D)

private val LightColorPalette = lightColors(
    primary = OrangePrimary,
    primaryVariant = OrangePrimaryDark,
    secondary = OrangePrimaryLight,
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    onPrimary = Color.White,
    onSurface = Color(0xFF212121),
    onBackground = Color(0xFF212121),
)

@Composable
fun HowToCookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        content = content
    )
}

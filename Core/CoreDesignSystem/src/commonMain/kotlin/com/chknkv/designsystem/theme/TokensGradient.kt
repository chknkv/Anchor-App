package com.chknkv.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush

/**
 * Enumeration of gradient tokens.
 *
 * These tokens provide predefined linear or radial gradients for icons, backgrounds,
 * and other decorative UI elements.
 */
enum class TokensGradient {
    Black,
    Gray,
    Blue,
    Indigo,
    Purple,
    Green,
    Orange,
    DarkOrange,
    Red,
    DarkRed,
    Pink,
}

/**
 * Returns the gradient brush associated with the current [TokensGradient] value based on the active theme.
 */
@Composable
@ReadOnlyComposable
fun TokensGradient.getThemedGradient(): Brush = Theme.gradients.getValue(this)

internal val LocalTokensGradient: ProvidableCompositionLocal<Map<TokensGradient, Brush>> =
    staticCompositionLocalOf { lightThemeGradientsMap() }

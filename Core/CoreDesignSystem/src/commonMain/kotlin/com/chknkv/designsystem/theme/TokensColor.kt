package com.chknkv.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Enumeration of named color tokens.
 *
 * These tokens represent specific color accents used for categories, status indicators,
 * or decorative elements where a particular color is required regardless of its semantic role.
 */
enum class TokensColor {
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
 * Returns the color associated with the current [TokensColor] value based on the active theme.
 */
@Composable
@ReadOnlyComposable
fun TokensColor.getThemedColor(): Color = Theme.colors.getValue(this)

internal val LocalTokensColor: ProvidableCompositionLocal<Map<TokensColor, Color>> =
    staticCompositionLocalOf { lightThemeColorsMap() }

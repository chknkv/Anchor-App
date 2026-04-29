package com.chknkv.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Объект для доступа к текущим цветам и градиентам темы.
 */
object Theme {
    /** Основные семантические токены (текст, фон, акценты). */
    val tokens: Map<Tokens, Color>
        @Composable
        @ReadOnlyComposable
        get() = LocalTokens.current

    /** Градиентные токены. */
    val gradients: Map<TokensGradient, Brush>
        @Composable
        @ReadOnlyComposable
        get() = LocalTokensGradient.current

    /** Статические цветовые токены. */
    val colors: Map<TokensColor, Color>
        @Composable
        @ReadOnlyComposable
        get() = LocalTokensColor.current
}

/**
 * Корневой компонент темы, предоставляющий токены дизайн-системы через CompositionLocal.
 * 
 * Автоматически определяет системную тему, если [darkTheme] не задан явно.
 * 
 * @param darkTheme Принудительная установка тёмной (true) или светлой (false) темы. 
 * Если null — используется системная тема.
 * @param content Иерархия компонентов, к которой будет применена тема.
 */
@Composable
fun Theme(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    val isDark = darkTheme ?: isSystemInDarkTheme()
    SystemBarsAppearance(isDark = isDark)
    CompositionLocalProvider(
        LocalTokens provides getColorsThemeTokens(isDark),
        LocalTokensGradient provides getGradientsThemeTokens(isDark),
        LocalTokensColor provides getStaticColorsThemeTokens(isDark)
    ) {
        content()
    }
}

/** Selecting current theme tokens */
@Composable
private fun getColorsThemeTokens(isDarkTheme: Boolean): Map<Tokens, Color> =
    if (isDarkTheme) darkThemeTokensMap() else lightThemeTokensMap()

/** Selecting current theme gradients */
@Composable
private fun getGradientsThemeTokens(isDarkTheme: Boolean): Map<TokensGradient, Brush> =
    if (isDarkTheme) darkThemeGradientsMap() else lightThemeGradientsMap()

/** Selecting current theme static colors */
@Composable
private fun getStaticColorsThemeTokens(isDarkTheme: Boolean): Map<TokensColor, Color> =
    if (isDarkTheme) darkThemeColorsMap() else lightThemeColorsMap()

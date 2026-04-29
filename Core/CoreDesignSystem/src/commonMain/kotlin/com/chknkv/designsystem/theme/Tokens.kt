package com.chknkv.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Enumeration of semantic design tokens.
 *
 * These tokens represent functional roles of colors in the application, such as backgrounds,
 * text, icons, and interactive elements. Using semantic tokens ensures consistency
 * when switching between light and dark themes.
 */
enum class Tokens {
    Background,                 // Основной цвет заднего фона приложения
    BackgroundSheet,            // Цвет фона для модальных штор
    Module,                     // Основной цвет для модуля-карточки
    TextPrimary,                // Основной текст
    TextSecondary,              // Вторичный текст
    IconPrimary,                // Основной цвет иконок
    IconSecondary,              // Вторичный цвет иконок
    Separator,                  // Цвет разделителя
    Action,                     // Цвет для элементов действий
    Warning,                    // Цвет для предупреждающих элементов
    Switcher,                   // Цвет для переключателя
    Shimmer,                    // Цвет для шиммера
    ShimmerHighlight,           // Цвет блика для шиммера
    ProgressTrack,              // Цвет фоновой дорожки кольца прогресса
    HudBackground,              // Цвет фона HUD-оверлея загрузки
    ChipBackgroundSelected,     // Цвет фона выбранной чипсы
    ChipBackgroundUnselected,   // Цвет фона не выбранной чипсы
    ChipContentSelected,        // Цвет контента выбранной чипсы
    ChipContentUnselected,      // Цвет контента не выбранной чипсы
    PasscodeKeyGlass,           // Цвет стеклянного фона клавиши passcode
}

/**
 * Returns the color associated with the current [Tokens] value based on the active theme.
 */
@Composable
@ReadOnlyComposable
fun Tokens.getThemedColor(): Color = Theme.tokens.getValue(this)

internal val LocalTokens: ProvidableCompositionLocal<Map<Tokens, Color>> =
    staticCompositionLocalOf { lightThemeTokensMap() }

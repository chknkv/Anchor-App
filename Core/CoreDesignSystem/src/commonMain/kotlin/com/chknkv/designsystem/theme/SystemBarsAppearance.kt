package com.chknkv.designsystem.theme

import androidx.compose.runtime.Composable

/**
 * Управляет цветом системных статус-баров в зависимости от темы.
 * 
 * @param isDark true — для тёмной темы (белые иконки), false — для светлой (тёмные иконки).
 */
@Composable
expect fun SystemBarsAppearance(isDark: Boolean)

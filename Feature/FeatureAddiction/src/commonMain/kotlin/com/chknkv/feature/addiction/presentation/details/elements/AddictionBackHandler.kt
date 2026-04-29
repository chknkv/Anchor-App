package com.chknkv.feature.addiction.presentation.details.elements

import androidx.compose.runtime.Composable

/**
 * Платформозависимый обработчик системной кнопки "Назад" для экрана деталей.
 *
 * @param enabled true, если обработчик активен (перехватывает событие).
 * @param onBack Колбэк, вызываемый при нажатии "Назад".
 */
@Composable
internal expect fun AddictionBackHandler(enabled: Boolean, onBack: () -> Unit)

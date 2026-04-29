package com.chknkv.corepasscode.presentation

import androidx.compose.runtime.Composable

/**
 * Обработчик системного события "Назад" (кнопка или жест свайпа).
 * 
 * Позволяет управлять поведением навигации на уровне платформы.
 * 
 * @param enabled Флаг активности обработчика.
 * @param onBack Действие, выполняемое при попытке пользователя вернуться назад.
 */
@Composable
internal expect fun PasscodeBackHandler(enabled: Boolean, onBack: () -> Unit)

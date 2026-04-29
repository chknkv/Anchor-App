package com.chknkv.designsystem.passcode

import androidx.compose.runtime.Composable

/**
 * Возвращает лямбду для воспроизведения системного звука нажатия клавиши.
 * Реализация зависит от платформы (Android/iOS).
 */
@Composable
expect fun rememberKeyClickPlayer(): () -> Unit

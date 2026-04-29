package com.chknkv.corepasscode.presentation.keyboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.passcode.PasscodeIndicator
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * Обёртка над [PasscodeIndicator] из CoreDesignSystem с shake-анимацией при ошибке.
 *
 * Shake запускается каждый раз, когда [shakeTrigger] меняется.
 *
 * @param length Общее число разрядов.
 * @param filledCount Число введённых цифр.
 * @param shakeTrigger Счётчик ошибок. Инкремент запускает тряску.
 * @param isError Подсветка индикатора цветом ошибки.
 * @param modifier Модификатор.
 */
@Composable
fun PasscodeIndicatorWithShake(
    length: Int,
    filledCount: Int,
    shakeTrigger: Int,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val offsetX = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger == 0) return@LaunchedEffect
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        offsetX.snapTo(0f)
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 400
                0f at 0
                (-8f) at 60
                8f at 120
                (-8f) at 180
                8f at 240
                (-4f) at 300
                0f at 400
            }
        )
    }

    val activeColor: Color = if (isError) Tokens.Warning.getThemedColor()
    else Tokens.Action.getThemedColor()

    PasscodeIndicator(
        length = length,
        filledCount = filledCount,
        modifier = modifier.offset(x = offsetX.value.dp),
        activeColor = activeColor,
    )
}

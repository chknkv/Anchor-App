package com.chknkv.designsystem.button

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.Headline
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * Универсальный компонент кнопки с поддержкой различных стилей, обработки нажатий 
 * и опционального описания.
 * 
 * Кнопка включает анимацию нажатия (масштаб и прозрачность) и тактильную отдачу (haptic).
 * 
 * @param text Текст, отображаемый на кнопке.
 * @param style Визуальный стиль кнопки ([ButtonStyle]).
 * @param onClick Коллбэк, вызываемый при нажатии.
 * @param modifier Модификатор для настройки макета кнопки.
 * @param enabled Флаг доступности кнопки для взаимодействия.
 * @param description Опциональный пояснительный текст под кнопкой (сноска).
 */
@Composable
fun Button(
    text: String,
    style: ButtonStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    description: String? = null
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedAlpha = animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "alpha"
    )

    val animatedScale = animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "scale"
    )

    val backgroundColor = style.backgroundColor
    val contentColor = style.contentColor

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .graphicsLayer {
                    scaleX = animatedScale.value
                    scaleY = animatedScale.value
                    alpha = if (enabled) animatedAlpha.value else 0.4f
                }
                .clip(RoundedCornerShape(25.dp))
                .background(backgroundColor)
                .pointerInput(enabled) {
                    if (enabled) {
                        detectTapGestures(
                            onPress = { offset ->
                                val press = PressInteraction.Press(offset)
                                interactionSource.emit(press)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                tryAwaitRelease()
                                interactionSource.emit(PressInteraction.Release(press))
                            },
                            onTap = {
                                onClick()
                            }
                        )
                    }
                }
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Headline(
                text = text,
                color = contentColor
            )
        }

        if (description != null) {
            Footnote(
                text = description,
                modifier = Modifier.padding(start = 16.dp, top = 6.dp, end = 16.dp),
                maxLines = 3,
                isSecondary = true
            )
        }
    }
}

/**
 * Интерфейс, определяющий цветовую схему для [Button].
 */
sealed interface ButtonStyle {
    /**
     * Цвет фона кнопки.
     */
    val backgroundColor: Color @Composable get

    /**
     * Цвет контента (текста) кнопки.
     */
    val contentColor: Color @Composable get

    /**
     * Акцентный стиль: используется для основных действий.
     */
    data object Action : ButtonStyle {
        override val backgroundColor: Color @Composable get() = Tokens.Action.getThemedColor()
        override val contentColor: Color @Composable get() = Color.White
    }

    /**
     * Стиль предупреждения: используется для деструктивных или предостерегающих действий.
     */
    data object Warning : ButtonStyle {
        override val backgroundColor: Color @Composable get() = Tokens.Module.getThemedColor()
        override val contentColor: Color @Composable get() = Tokens.Warning.getThemedColor()
    }

    /**
     * Стандартный стиль: неброский фон с основным цветом текста.
     */
    data object Default : ButtonStyle {
        override val backgroundColor: Color @Composable get() = Tokens.Module.getThemedColor()
        override val contentColor: Color @Composable get() = Tokens.TextPrimary.getThemedColor()
    }

    /**
     * Пользовательский стиль: позволяет задать произвольные цвета.
     * 
     * @param customBackgroundColor Цвет фона.
     * @param customContentColor Цвет текста.
     */
    data class Custom(
        val customBackgroundColor: Color,
        val customContentColor: Color
    ) : ButtonStyle {
        override val backgroundColor: Color @Composable get() = customBackgroundColor
        override val contentColor: Color @Composable get() = customContentColor
    }
}

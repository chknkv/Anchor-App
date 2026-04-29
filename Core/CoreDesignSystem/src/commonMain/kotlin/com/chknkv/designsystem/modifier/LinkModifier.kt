package com.chknkv.designsystem.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * Добавляет тексту поведение ссылки: подчеркивание и обработку нажатия.
 * 
 * Данный вариант учитывает макет текста для корректного подчеркивания многострочных ссылок.
 * 
 * @param textLayoutResult Состояние с результатом отрисовки текста (для определения границ строк).
 * @param enabled Флаг доступности ссылки.
 * @param color Цвет подчеркивания.
 * @param onAction Коллбэк при нажатии.
 */
fun Modifier.link(
    textLayoutResult: State<TextLayoutResult?>,
    enabled: Boolean = true,
    color: Color? = null,
    onAction: () -> Unit
): Modifier = composed {
    val lineColor = color ?: Tokens.TextSecondary.getThemedColor()
    val interactionSource = remember { MutableInteractionSource() }

    this
        .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            onClick = onAction
        )
        .drawBehind {
            val strokeWidth = 0.5.dp.toPx()
            val layout = textLayoutResult.value

            if (layout != null) {
                for (i in 0 until layout.lineCount) {
                    val lineLeft = layout.getLineLeft(i)
                    val lineRight = layout.getLineRight(i)
                    val lineBottom = layout.getLineBottom(i)
                    drawLine(
                        color = lineColor.copy(alpha = if (enabled) 1f else 0.5f),
                        start = Offset(lineLeft, lineBottom - strokeWidth / 2),
                        end = Offset(lineRight, lineBottom - strokeWidth / 2),
                        strokeWidth = strokeWidth
                    )
                }
            } else {
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = lineColor.copy(alpha = if (enabled) 1f else 0.5f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
        }
}

/**
 * Упрощенный вариант [link] без учета макета текста. Подчеркивает всю область компонента.
 * 
 * @param enabled Флаг доступности ссылки.
 * @param color Цвет подчеркивания.
 * @param onAction Коллбэк при нажатии.
 */
fun Modifier.link(
    enabled: Boolean = true,
    color: Color? = null,
    onAction: () -> Unit
): Modifier = link(
    textLayoutResult = object : State<TextLayoutResult?> { override val value = null },
    enabled = enabled,
    color = color,
    onAction = onAction
)

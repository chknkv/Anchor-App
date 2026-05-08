package com.chknkv.designsystem.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * Описывает один кликабельный сегмент внутри текста.
 *
 * Используется совместно с перегрузкой [Modifier.link], принимающей список [LinkSegment],
 * когда внутри одного текстового компонента необходимо несколько независимых ссылок
 * с разными обработчиками нажатия.
 *
 * @property range Диапазон символьных индексов (включительно с обеих сторон),
 *   соответствующий кликабельному подтексту.
 * @property onAction Коллбэк, вызываемый при нажатии на символ внутри [range].
 */
data class LinkSegment(
    val range: IntRange,
    val onAction: () -> Unit,
)

/**
 * Добавляет тексту поведение ссылки: подчёркивание всего компонента и единый обработчик нажатия.
 *
 * Учитывает макет текста ([textLayoutResult]) для корректного подчёркивания
 * каждой строки в многострочном тексте.
 *
 * @param textLayoutResult Состояние с результатом отрисовки текста. Передаётся через
 *   параметр `onTextLayout` типографического компонента.
 * @param enabled Если `false` — нажатие игнорируется, подчёркивание рисуется с `alpha = 0.5`.
 * @param color Цвет подчёркивания. По умолчанию — [Tokens.TextSecondary].
 * @param onAction Коллбэк при нажатии на компонент.
 */
fun Modifier.link(
    textLayoutResult: State<TextLayoutResult?>,
    enabled: Boolean = true,
    color: Color? = null,
    onAction: () -> Unit
): Modifier = linkImpl(
    textLayoutResult = textLayoutResult,
    ranges = null,
    enabled = enabled,
    color = color,
    onAction = onAction
)

/**
 * Добавляет тексту поведение ссылки: подчёркивание одного символьного диапазона
 * и единый обработчик нажатия на весь компонент.
 *
 * @param textLayoutResult Состояние с результатом отрисовки текста. Передаётся через
 *   параметр `onTextLayout` типографического компонента.
 * @param range Диапазон символьных индексов (включительно с обеих сторон),
 *   который будет подчёркнут. Корректно обрабатывает перенос на следующую строку.
 * @param enabled Если `false` — нажатие игнорируется, подчёркивание рисуется с `alpha = 0.5`.
 * @param color Цвет подчёркивания. По умолчанию — [Tokens.TextSecondary].
 * @param onAction Коллбэк при нажатии на компонент.
 */
fun Modifier.link(
    textLayoutResult: State<TextLayoutResult?>,
    range: IntRange,
    enabled: Boolean = true,
    color: Color? = null,
    onAction: () -> Unit
): Modifier = linkImpl(
    textLayoutResult = textLayoutResult,
    ranges = listOf(range),
    enabled = enabled,
    color = color,
    onAction = onAction
)

/**
 * Добавляет тексту поведение ссылки: подчёркивание нескольких символьных диапазонов
 * и единый обработчик нажатия на весь компонент.
 *
 * Все диапазоны подчёркиваются одним цветом; нажатие в любую точку компонента
 * вызывает один и тот же [onAction]. Для независимых обработчиков на каждый диапазон
 * используйте перегрузку с [List] из [LinkSegment].
 *
 * @param textLayoutResult Состояние с результатом отрисовки текста. Передаётся через
 *   параметр `onTextLayout` типографического компонента.
 * @param ranges Список диапазонов символьных индексов (включительно с обеих сторон).
 *   Каждый диапазон подчёркивается отдельно; диапазоны могут не быть смежными.
 * @param enabled Если `false` — нажатие игнорируется, подчёркивание рисуется с `alpha = 0.5`.
 * @param color Цвет подчёркивания. По умолчанию — [Tokens.TextSecondary].
 * @param onAction Коллбэк при нажатии на компонент.
 */
fun Modifier.link(
    textLayoutResult: State<TextLayoutResult?>,
    ranges: List<IntRange>,
    enabled: Boolean = true,
    color: Color? = null,
    onAction: () -> Unit
): Modifier = linkImpl(
    textLayoutResult = textLayoutResult,
    ranges = ranges,
    enabled = enabled,
    color = color,
    onAction = onAction
)

/**
 * Добавляет тексту поведение нескольких независимых ссылок: каждый [LinkSegment]
 * подчёркивается отдельно и имеет собственный обработчик нажатия.
 *
 * Нажатие определяется по символьному смещению в точке касания
 * через [TextLayoutResult.getOffsetForPosition]. Тап вне любого сегмента игнорируется.
 *
 * @param textLayoutResult Состояние с результатом отрисовки текста. Передаётся через
 *   параметр `onTextLayout` типографического компонента.
 * @param segments Список сегментов, каждый из которых задаёт диапазон символов
 *   и соответствующий обработчик. Сегменты обрабатываются в порядке списка;
 *   при перекрытии диапазонов срабатывает первый подходящий.
 * @param enabled Если `false` — нажатия игнорируются, подчёркивания рисуются с `alpha = 0.5`.
 * @param color Цвет подчёркивания для всех сегментов. По умолчанию — [Tokens.TextSecondary].
 */
fun Modifier.link(
    textLayoutResult: State<TextLayoutResult?>,
    segments: List<LinkSegment>,
    enabled: Boolean = true,
    color: Color? = null,
): Modifier = composed {
    val lineColor = color ?: Tokens.TextSecondary.getThemedColor()

    this
        .pointerInput(segments, enabled) {
            detectTapGestures { tapOffset ->
                if (!enabled) return@detectTapGestures
                val layout = textLayoutResult.value ?: return@detectTapGestures
                val charOffset = layout.getOffsetForPosition(tapOffset)
                segments.firstOrNull { charOffset in it.range }?.onAction?.invoke()
            }
        }
        .drawBehind {
            val strokeWidth = 0.5.dp.toPx()
            val layout = textLayoutResult.value
            val resolvedColor = lineColor.copy(alpha = if (enabled) 1f else 0.5f)

            if (layout != null) {
                segments.forEach { segment ->
                    drawRangeUnderline(layout, segment.range, resolvedColor, strokeWidth)
                }
            }
        }
}

/**
 * Упрощённая перегрузка [link] без учёта макета текста.
 *
 * Подчёркивает всю ширину компонента единой линией по нижней границе.
 * Используется когда [TextLayoutResult] недоступен или не нужен.
 *
 * @param enabled Если `false` — нажатие игнорируется, подчёркивание рисуется с `alpha = 0.5`.
 * @param color Цвет подчёркивания. По умолчанию — [Tokens.TextSecondary].
 * @param onAction Коллбэк при нажатии на компонент.
 */
fun Modifier.link(
    enabled: Boolean = true,
    color: Color? = null,
    onAction: () -> Unit
): Modifier = linkImpl(
    textLayoutResult = object : State<TextLayoutResult?> { override val value = null },
    ranges = null,
    enabled = enabled,
    color = color,
    onAction = onAction
)

/**
 * Общая реализация для перегрузок [link] с единым [onAction].
 *
 * Делает весь компонент кликабельным и рисует подчёркивания:
 * - `ranges == null` или пустой → подчёркивает каждую строку целиком через [drawFullUnderline].
 * - иначе → подчёркивает только указанные диапазоны через [drawRangeUnderline].
 * - `textLayoutResult.value == null` → рисует единую линию по нижней границе компонента.
 *
 * @param textLayoutResult Состояние с результатом отрисовки текста.
 * @param ranges Диапазоны для подчёркивания; `null` означает «подчеркнуть всё».
 * @param enabled Флаг активности.
 * @param color Цвет подчёркивания.
 * @param onAction Коллбэк при нажатии.
 */
private fun Modifier.linkImpl(
    textLayoutResult: State<TextLayoutResult?>,
    ranges: List<IntRange>?,
    enabled: Boolean,
    color: Color?,
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
            val resolvedColor = lineColor.copy(alpha = if (enabled) 1f else 0.5f)

            if (layout != null) {
                if (ranges.isNullOrEmpty()) {
                    drawFullUnderline(layout, resolvedColor, strokeWidth)
                } else {
                    ranges.forEach { range ->
                        drawRangeUnderline(layout, range, resolvedColor, strokeWidth)
                    }
                }
            } else {
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = resolvedColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
        }
}

/**
 * Рисует подчёркивание под каждой строкой [layout] от левого до правого края.
 *
 * @param layout Результат отрисовки текста.
 * @param color Цвет линии.
 * @param strokeWidth Толщина линии в пикселях.
 */
private fun DrawScope.drawFullUnderline(
    layout: TextLayoutResult,
    color: Color,
    strokeWidth: Float
) {
    for (i in 0 until layout.lineCount) {
        val y = layout.getLineBottom(i) - strokeWidth / 2
        drawLine(
            color = color,
            start = Offset(layout.getLineLeft(i), y),
            end = Offset(layout.getLineRight(i), y),
            strokeWidth = strokeWidth
        )
    }
}

/**
 * Рисует подчёркивание под символами [layout], попадающими в [range].
 *
 * Корректно обрабатывает перенос диапазона на несколько строк: для каждой строки
 * вычисляется пересечение с [range], после чего горизонтальные границы
 * определяются через [TextLayoutResult.getHorizontalPosition].
 * Индексы за пределами текста безопасно зажимаются через [coerceIn].
 *
 * @param layout Результат отрисовки текста.
 * @param range Диапазон символьных индексов (включительно с обеих сторон).
 * @param color Цвет линии.
 * @param strokeWidth Толщина линии в пикселях.
 */
private fun DrawScope.drawRangeUnderline(
    layout: TextLayoutResult,
    range: IntRange,
    color: Color,
    strokeWidth: Float
) {
    val textLength = layout.layoutInput.text.length
    val startIndex = range.first.coerceIn(0, textLength)
    val endIndex = (range.last + 1).coerceIn(startIndex, textLength)
    if (startIndex >= endIndex) return

    val startLine = layout.getLineForOffset(startIndex)
    val endLine = layout.getLineForOffset(endIndex - 1)

    for (lineIndex in startLine..endLine) {
        val lineStart = layout.getLineStart(lineIndex)
        val lineEnd = layout.getLineEnd(lineIndex)

        val rangeStart = maxOf(startIndex, lineStart)
        val rangeEnd = minOf(endIndex, lineEnd)
        if (rangeStart >= rangeEnd) continue

        val xStart = layout.getHorizontalPosition(rangeStart, true)
        val xEnd = layout.getHorizontalPosition(rangeEnd, true)
        val y = layout.getLineBottom(lineIndex) - strokeWidth / 2

        drawLine(
            color = color,
            start = Offset(xStart, y),
            end = Offset(xEnd, y),
            strokeWidth = strokeWidth
        )
    }
}

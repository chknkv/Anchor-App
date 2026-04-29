package com.chknkv.designsystem.cell

import com.chknkv.designsystem.Separator
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chknkv.designsystem.Body
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * A selectable list item component (Picker Cell) that displays a title and a checkmark when selected.
 *
 * Provides built-in haptic feedback when the [isSelected] state changes.
 *
 * @param title The text to be displayed in the cell.
 * @param isSelected Whether the cell is currently selected (shows checkmark).
 * @param modifier The modifier to be applied to the cell's layout.
 * @param isDivider Whether to display a [Separator] at the bottom of the cell.
 * @param isHapticFeedbackEnabled Whether to trigger haptic feedback on selection change.
 * @param hapticFeedbackType The type of haptic feedback to perform.
 * @param onClick Lambda to be invoked when the cell is clicked.
 */
@Composable
fun CellPicker(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    isDivider: Boolean = true,
    isHapticFeedbackEnabled: Boolean = true,
    hapticFeedbackType: HapticFeedbackType? = null,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    var isFirstComposition by remember { mutableStateOf(true) }

    LaunchedEffect(isSelected) {
        if (isFirstComposition) {
            isFirstComposition = false
            return@LaunchedEffect
        }
        if (isHapticFeedbackEnabled) {
            haptic.performHapticFeedback(hapticFeedbackType ?: HapticFeedbackType.SegmentTick)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier.size(34.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconCheckmark(color = Tokens.Action.getThemedColor())
                }
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Spacer(modifier = Modifier.width(42.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = with(density) { 8.sp.toDp() })
            ) {
                Body(text = title, maxLines = 1)
            }
        }

        if (isDivider) {
            Separator(leadingInset = 42.dp)
        }
    }
}

/**
 * A custom-drawn checkmark icon used in [CellPicker].
 *
 * @param color The color of the checkmark.
 * @param modifier The modifier for the canvas.
 */
@Composable
private fun IconCheckmark(
    color: Color,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val path = remember(density) {
        Path().apply {
            moveTo(1.5.dp.toPx(density), 6.5.dp.toPx(density))
            lineTo(5.5.dp.toPx(density), 10.5.dp.toPx(density))
            lineTo(14.5.dp.toPx(density), 1.5.dp.toPx(density))
        }
    }

    Canvas(modifier = modifier.size(16.dp, 12.dp)) {
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 1.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * Extension to convert DP to PX using a provided density.
 */
private fun androidx.compose.ui.unit.Dp.toPx(density: androidx.compose.ui.unit.Density): Float = 
    with(density) { this@toPx.toPx() }

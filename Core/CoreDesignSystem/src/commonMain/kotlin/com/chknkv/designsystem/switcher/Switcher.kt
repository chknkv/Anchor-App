package com.chknkv.designsystem.switcher

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import kotlin.math.roundToInt

private val TrackWidth = 70.dp
private val TrackHeight = 31.dp
private val ThumbWidth = 43.dp
private val ThumbHeight = 27.dp
private val ThumbPadding = 2.dp
private val ThumbPressedWidth = 51.dp

/**
 * A switcher (toggle) component.
 *
 * Features smooth animations for state changes, thumb stretching on press,
 * and haptic feedback integration.
 *
 * @param checked Whether the switch is currently on or off.
 * @param onCheckedChange Lambda to be invoked when the switch state changes.
 * @param modifier The modifier to be applied to the switcher's layout.
 * @param onTintColor The background color when the switch is in the [checked] state.
 * @param enabled Whether the switcher is interactive and enabled.
 */
@Composable
fun Switcher(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    onTintColor: Color = Tokens.Switcher.getThemedColor(),
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val targetTrackColor = when {
        checked -> onTintColor
        isSystemInDarkTheme() -> Color(0xFF39393D)
        else -> Color(0xFFE9E9EA)
    }

    val trackColorState = animateColorAsState(
        targetValue = targetTrackColor,
        label = "TrackColor"
    )

    val thumbWidthState = animateDpAsState(
        targetValue = if (isPressed && enabled) ThumbPressedWidth else ThumbWidth,
        animationSpec = spring(),
        label = "ThumbWidth"
    )

    val thumbOffsetState = animateDpAsState(
        targetValue = if (checked) {
            TrackWidth - ThumbPadding - thumbWidthState.value
        } else {
            ThumbPadding
        },
        animationSpec = spring(),
        label = "ThumbOffset"
    )

    Box(
        modifier = modifier
            .size(TrackWidth, TrackHeight)
            .graphicsLayer {
                alpha = if (enabled) 1f else 0.5f
            }
            .clip(CircleShape)
            .drawBehind {
                drawRoundRect(
                    color = trackColorState.value,
                    cornerRadius = CornerRadius(size.height / 2f)
                )
            }
            .toggleable(
                value = checked,
                onValueChange = {
                    haptic.performHapticFeedback(
                        if (checked) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                    )
                    onCheckedChange?.invoke(it)
                },
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffsetState.value.toPx().roundToInt(), 0) }
                .layout { measurable, _ ->
                    val width = thumbWidthState.value.roundToPx()
                    val height = ThumbHeight.roundToPx()
                    val placeable = measurable.measure(Constraints.fixed(width, height))
                    layout(width, height) {
                        placeable.placeRelative(0, 0)
                    }
                }
                .shadow(
                    elevation = if (enabled) 2.dp else 0.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.15f),
                    spotColor = Color.Black.copy(alpha = 0.15f)
                )
                .background(Color.White, CircleShape)
        )
    }
}

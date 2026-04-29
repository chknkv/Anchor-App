package com.chknkv.designsystem.chip

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * A high-fidelity, production-ready chip component that embodies the 'PlatformNative' aesthetic.
 *
 * @param text The text to be displayed on the chip.
 * @param isSelected Whether the chip is currently selected.
 * @param onActionHandler Callback triggered when the chip is clicked.
 * @param modifier The modifier to be applied to the chip.
 * @param isAnimateColor Whether to animate color transitions.
 * @param externalPadding Padding applied to the entire component.
 * @param internalHorizontalPadding Padding applied to the start of the leading element and the end of the trailing element.
 */
@Composable
fun Chip(
    text: String,
    isSelected: Boolean,
    onActionHandler: () -> Unit,
    modifier: Modifier = Modifier,
    isAnimateColor: Boolean = true,
    configuration: ChipConfiguration = ChipDefaults.configuration(),
    externalPadding: Dp = 4.dp,
    internalHorizontalPadding: Dp = 20.dp
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundBaseColor = if (isSelected) {
        configuration.selectedBackgroundColor
    } else {
        configuration.unselectedBackgroundColor
    }

    val contentBaseColor = if (isSelected) {
        configuration.selectedContentColor
    } else {
        configuration.unselectedContentColor
    }

    val backgroundColor by animateColorAsState(
        targetValue = backgroundBaseColor,
        animationSpec = tween(durationMillis = if (isAnimateColor) 250 else 0),
        label = "ChipBackgroundAnimation"
    )

    val contentColor by animateColorAsState(
        targetValue = contentBaseColor,
        animationSpec = tween(durationMillis = if (isAnimateColor) 250 else 0),
        label = "ChipContentAnimation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        label = "ChipScaleAnimation"
    )

    val shape = remember { RoundedCornerShape(50) }

    var isInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(isSelected) {
        if (isInitialized) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } else {
            isInitialized = true
        }
    }

    Box(
        modifier = modifier
            .sizeIn(minHeight = 45.dp, minWidth = 64.dp)
            .padding(externalPadding)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onActionHandler
            ),
        contentAlignment = Alignment.Center
    ) {
        Footnote(
            modifier = Modifier.padding(vertical = 11.dp, horizontal = internalHorizontalPadding),
            text = text,
            color = contentColor,
            maxLines = 1
        )
    }
}

/**
 * Configuration for the [Chip] component.
 *
 * @param selectedBackgroundColor The background color when the chip is selected.
 * @param unselectedBackgroundColor The background color when the chip is not selected.
 * @param selectedContentColor The content (text) color when the chip is selected.
 * @param unselectedContentColor The content (text) color when the chip is not selected.
 */
@Immutable
data class ChipConfiguration(
    val selectedBackgroundColor: Color,
    val unselectedBackgroundColor: Color,
    val selectedContentColor: Color,
    val unselectedContentColor: Color
)

/**
 * Contains the default values used by [Chip].
 */
object ChipDefaults {

    /**
     * Creates a [ChipConfiguration] with the default colors.
     */
    @Composable
    @ReadOnlyComposable
    fun configuration(
        selectedBackgroundColor: Color = Tokens.ChipBackgroundSelected.getThemedColor(),
        unselectedBackgroundColor: Color = Tokens.ChipBackgroundUnselected.getThemedColor(),
        selectedContentColor: Color = Tokens.ChipContentSelected.getThemedColor(),
        unselectedContentColor: Color = Tokens.ChipContentUnselected.getThemedColor()
    ): ChipConfiguration = ChipConfiguration(
        selectedBackgroundColor = selectedBackgroundColor,
        unselectedBackgroundColor = unselectedBackgroundColor,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor
    )
}
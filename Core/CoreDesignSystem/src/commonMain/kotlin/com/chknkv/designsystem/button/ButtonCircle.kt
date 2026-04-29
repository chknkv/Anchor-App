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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import anchor_app.core.coredesignsystem.generated.resources.Res
import anchor_app.core.coredesignsystem.generated.resources.ic_chevron_left

/**
 * A circular icon button component designed for compact actions like navigation or closing.
 *
 * Features smooth press animations (scale and alpha) and optional haptic feedback.
 *
 * @param modifier The modifier to be applied to the button's layout.
 * @param iconRes The drawable resource for the icon. Defaults to a chevron left icon.
 * @param iconColor The tint color for the icon. Defaults to [Tokens.IconPrimary].
 * @param buttonSize The overall diameter of the circular button.
 * @param backgroundColor The background color of the button. Defaults to [Tokens.Module].
 * @param hapticFeedbackType The type of haptic feedback to perform on press. Disabled if null.
 * @param onClick Lambda to be invoked when the button is clicked.
 */
@Composable
fun ButtonCircle(
    modifier: Modifier = Modifier,
    iconRes: DrawableResource? = null,
    iconColor: Color? = null,
    buttonSize: Dp = 42.dp,
    backgroundColor: Color? = null,
    hapticFeedbackType: HapticFeedbackType? = null,
    onClick: () -> Unit
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
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(buttonSize)
            .graphicsLayer {
                scaleX = animatedScale.value
                scaleY = animatedScale.value
                alpha = animatedAlpha.value
            }
            .shadow(2.dp, CircleShape)
            .background(
                color = backgroundColor ?: Tokens.Module.getThemedColor(),
                shape = CircleShape
            )
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val press = PressInteraction.Press(offset)
                        interactionSource.emit(press)
                        hapticFeedbackType?.let { haptic.performHapticFeedback(it) }
                        tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(press))
                    },
                    onTap = { onClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes ?: Res.drawable.ic_chevron_left),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = iconColor ?: Tokens.IconPrimary.getThemedColor()
        )
    }
}

package com.chknkv.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val IconShape = RoundedCornerShape(8.dp)

/**
 * A square icon component with a rounded background.
 *
 * Supports solid colors, gradients, and custom icon tinting.
 *
 * @param modifier The modifier to be applied to the icon's container.
 * @param iconSize The overall size of the square container.
 * @param iconRes The drawable resource for the icon.
 * @param iconResSize The size of the icon itself within the container.
 * @param backgroundColor The background color of the container. Defaults to [Tokens.IconSecondary] if neither color nor gradient is provided.
 * @param backgroundGradient Optional brush for the background gradient. Takes precedence over [backgroundColor].
 * @param iconTint The tint color for the icon. Defaults to [Color.White].
 */
@Composable
fun SquareIcon(
    modifier: Modifier = Modifier,
    iconSize: Dp = 28.dp,
    iconRes: DrawableResource,
    iconResSize: Dp = 20.dp,
    backgroundColor: Color? = null,
    backgroundGradient: Brush? = null,
    iconTint: Color = Color.White
) {
    val defaultBackgroundColor = Tokens.IconSecondary.getThemedColor()

    val backgroundModifier = when {
        backgroundGradient != null -> Modifier.background(brush = backgroundGradient, shape = IconShape)
        backgroundColor != null -> Modifier.background(color = backgroundColor, shape = IconShape)
        else -> Modifier.background(color = defaultBackgroundColor, shape = IconShape)
    }

    Box(
        modifier = modifier
            .size(iconSize)
            .then(backgroundModifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(iconResSize),
            tint = iconTint
        )
    }
}

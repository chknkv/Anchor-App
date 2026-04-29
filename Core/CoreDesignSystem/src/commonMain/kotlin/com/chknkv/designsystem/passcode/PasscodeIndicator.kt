package com.chknkv.designsystem.passcode

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * A row of dots/indicators representing the passcode entry state.
 *
 * @param length The total number of digits in the passcode.
 * @param filledCount The number of digits currently entered.
 * @param modifier The modifier to be applied to the row.
 * @param activeColor The color of a filled dot.
 * @param inactiveColor The color of an empty dot.
 */
@Composable
fun PasscodeIndicator(
    length: Int,
    filledCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = Tokens.Action.getThemedColor(),
    inactiveColor: Color = Tokens.Separator.getThemedColor()
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(length) { index ->
            val isFilled = index < filledCount
            
            val color by animateColorAsState(
                targetValue = if (isFilled) activeColor else inactiveColor,
                animationSpec = spring(),
                label = "colorAnimation"
            )
            
            val scale by animateFloatAsState(
                targetValue = if (isFilled) 1.2f else 1.0f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                label = "scaleAnimation"
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .scale(scale)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}

package com.chknkv.designsystem.modifier

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.theme.Theme
import com.chknkv.designsystem.theme.Tokens

/**
 * Adds an animated shimmer effect to the component.
 *
 * Typically used as a placeholder (skeleton) while content is loading.
 *
 * @param enabled Whether the shimmer effect is active.
 * @param shape The shape of the shimmer area.
 * @param colorOverride Optional base color for the shimmer. If null, the theme's default is used.
 */
fun Modifier.shimmer(
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(10.dp),
    colorOverride: Color? = null
): Modifier = if (enabled) {
    this.composed {
        val baseColor = colorOverride ?: Theme.tokens.getValue(Tokens.Shimmer)
        val highlightColor = Theme.tokens.getValue(Tokens.ShimmerHighlight)

        val transition = rememberInfiniteTransition(label = "shimmer")

        val translateAnimState = transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1300,
                    delayMillis = 200,
                    easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_translation"
        )

        val shimmerColors = remember(baseColor, highlightColor) {
            listOf(
                baseColor,
                baseColor,
                highlightColor,
                baseColor,
                baseColor
            )
        }

        this
            .clip(shape)
            .background(baseColor)
            .graphicsLayer(alpha = 0.99f)
            .drawWithContent {
                drawContent()
                val width = size.width
                val height = size.height
                
                val dx = height / 2.75f
                val glowWidth = width * 1.4f
                val totalDistance = width + glowWidth + dx
                
                val currentX = -glowWidth - dx + (totalDistance * translateAnimState.value)

                val brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(currentX, 0f),
                    end = Offset(currentX + glowWidth + dx, height)
                )
                drawRect(brush = brush)
            }
    }
} else {
    this
}

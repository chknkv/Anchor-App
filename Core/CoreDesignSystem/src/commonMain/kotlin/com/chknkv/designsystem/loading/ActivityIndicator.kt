package com.chknkv.designsystem.loading

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Gray2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The number of spokes drawn by the activity indicator.
 */
private const val SPOKE_COUNT = 12

/**
 * Duration of one full rotation cycle in milliseconds.
 */
private const val ROTATION_DURATION_MS = 1000

/**
 * A spinning activity indicator composed of radial spokes.
 *
 * Draws [SPOKE_COUNT] radial spokes arranged in a circle. Each spoke fades in and out
 * sequentially, creating a discrete step-rotation effect characteristic of a system
 * activity indicator.
 *
 * @param modifier Modifier to be applied to the indicator layout. Use `Modifier.size()` to
 *   override the default dimensions.
 * @param color Tint color for the spokes. Defaults to `systemGray` (`0xFF8E8E93`).
 * @param size Predefined size variant — either [ActivityIndicatorSize.Small] (20 dp) or
 *   [ActivityIndicatorSize.Large] (37 dp).
 */
@Composable
fun ActivityIndicator(
    modifier: Modifier = Modifier,
    color: Color = Gray2,
    size: ActivityIndicatorSize = ActivityIndicatorSize.Large
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")

    val animationSpec = remember {
        infiniteRepeatable<Float>(
            animation = tween(
                durationMillis = ROTATION_DURATION_MS,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    }

    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = animationSpec,
        label = "spinner_progress"
    )

    val spokeAngles = remember {
        FloatArray(SPOKE_COUNT) { index ->
            (2f * PI.toFloat() / SPOKE_COUNT) * index - (PI.toFloat() / 2f)
        }
    }

    val indicatorSize: Dp = size.sizeDp

    Canvas(
        modifier = modifier
            .size(indicatorSize)
            .graphicsLayer { clip = false }
    ) {
        val canvasWidth = this.size.width
        val canvasHeight = this.size.height
        val centerX = canvasWidth / 2f
        val centerY = canvasHeight / 2f
        val radius = minOf(centerX, centerY)

        val strokeWidth = radius * 0.16f
        val innerRadius = radius * 0.38f
        val outerRadius = radius * 0.80f

        val activeSpoke = (animatedProgress * SPOKE_COUNT).toInt().coerceIn(0, SPOKE_COUNT - 1)

        for (i in 0 until SPOKE_COUNT) {
            val angle = spokeAngles[i]

            val distanceFromActive = ((i - activeSpoke + SPOKE_COUNT) % SPOKE_COUNT)
            val alpha = 1f - (distanceFromActive.toFloat() / SPOKE_COUNT) * 0.8f

            val startX = centerX + innerRadius * cos(angle)
            val startY = centerY + innerRadius * sin(angle)
            val endX = centerX + outerRadius * cos(angle)
            val endY = centerY + outerRadius * sin(angle)

            drawLine(
                color = color.copy(alpha = alpha.coerceIn(0.2f, 1f)),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Predefined sizes for [ActivityIndicator].
 *
 * @property sizeDp The side length of the indicator in [Dp].
 */
enum class ActivityIndicatorSize(val sizeDp: Dp) {
    /** Compact size (20 dp), suitable for inline use. */
    Small(20.dp),
    /** Standard size (37 dp), used as the default. */
    Large(37.dp)
}

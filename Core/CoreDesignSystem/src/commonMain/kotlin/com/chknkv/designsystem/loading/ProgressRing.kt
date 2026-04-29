package com.chknkv.designsystem.loading

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * Default ring diameter.
 */
private val DEFAULT_SIZE = 32.dp

/**
 * Stroke width used for drawing the circular track and the progress arc.
 */
private val STROKE_WIDTH = 3.dp

/**
 * Duration of one full indeterminate rotation cycle in milliseconds.
 */
private const val INDETERMINATE_ROTATION_DURATION_MS = 1100

/**
 * Duration of one indeterminate sweep (arc length) cycle in milliseconds.
 */
private const val INDETERMINATE_SWEEP_DURATION_MS = 1400

/**
 * Minimum arc sweep angle in the indeterminate animation.
 */
private const val MIN_SWEEP_ANGLE = 30f

/**
 * Maximum arc sweep angle in the indeterminate animation.
 */
private const val MAX_SWEEP_ANGLE = 270f

/**
 * A circular progress ring with determinate and indeterminate modes.
 *
 * **Determinate mode**: Pass a non-null [progress] value between `0f` and `1f`.
 * The arc will animate smoothly to the specified progress. A subtle background track
 * is drawn to indicate the full circle.
 *
 * **Indeterminate mode**: Pass `null` for [progress]. The ring animates continuously
 * with a sweeping arc that grows and shrinks while rotating — similar to a download
 * progress indicator.
 *
 * @param modifier Modifier to be applied to the canvas layout.
 * @param progress Current progress (`0f`–`1f`), or `null` for indeterminate mode.
 * @param color Stroke color for the progress arc. Defaults to the theme action color.
 * @param trackColor Background track color, visible in determinate mode. Defaults to
 *   the theme progress track color.
 */
@Composable
fun ProgressRing(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    color: Color = Tokens.Action.getThemedColor(),
    trackColor: Color = Tokens.ProgressTrack.getThemedColor()
) {
    if (progress != null) {
        DeterminateProgressRing(
            modifier = modifier,
            progress = progress,
            color = color,
            trackColor = trackColor
        )
    } else {
        IndeterminateProgressRing(
            modifier = modifier,
            color = color
        )
    }
}

/**
 * Circular progress ring that animates to a fixed [progress] value.
 */
@Composable
private fun DeterminateProgressRing(
    modifier: Modifier,
    progress: Float,
    color: Color,
    trackColor: Color
) {
    val animationSpec = remember {
        tween<Float>(durationMillis = 300, easing = LinearEasing)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = animationSpec,
        label = "determinate_progress"
    )

    Canvas(
        modifier = modifier
            .size(DEFAULT_SIZE)
            .graphicsLayer { clip = false }
    ) {
        val strokeWidthPx = STROKE_WIDTH.toPx()
        val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        val arcSize = androidx.compose.ui.geometry.Size(
            width = size.width - strokeWidthPx,
            height = size.height - strokeWidthPx
        )
        val topLeft = androidx.compose.ui.geometry.Offset(
            x = strokeWidthPx / 2f,
            y = strokeWidthPx / 2f
        )

        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke
        )

        val sweepAngle = 360f * animatedProgress
        if (sweepAngle > 0f) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
        }
    }
}

/**
 * Continuously animating progress ring with a sweeping arc.
 *
 * Both the rotation angle and arc length are animated independently to produce the
 * characteristic "breathing" effect.
 */
@Composable
private fun IndeterminateProgressRing(
    modifier: Modifier,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "indeterminate_ring")

    val rotationSpec = remember {
        infiniteRepeatable<Float>(
            animation = tween(
                durationMillis = INDETERMINATE_ROTATION_DURATION_MS,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    }

    val sweepSpec = remember {
        infiniteRepeatable<Float>(
            animation = tween(
                durationMillis = INDETERMINATE_SWEEP_DURATION_MS,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    }

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = rotationSpec,
        label = "rotation"
    )

    val sweepProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = sweepSpec,
        label = "sweep"
    )

    val sweepAngle = remember(sweepProgress) {
        val normalized = if (sweepProgress <= 0.5f) {
            sweepProgress * 2f
        } else {
            (1f - sweepProgress) * 2f
        }
        MIN_SWEEP_ANGLE + (MAX_SWEEP_ANGLE - MIN_SWEEP_ANGLE) * normalized
    }

    Canvas(
        modifier = modifier
            .size(DEFAULT_SIZE)
            .graphicsLayer { clip = false }
    ) {
        val strokeWidthPx = STROKE_WIDTH.toPx()
        val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)

        drawArc(
            color = color,
            startAngle = rotationAngle - 90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = stroke
        )
    }
}

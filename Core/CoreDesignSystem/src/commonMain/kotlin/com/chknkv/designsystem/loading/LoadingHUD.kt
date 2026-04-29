package com.chknkv.designsystem.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * Corner radius for the HUD container.
 */
private val HudCornerRadius = 20.dp

/**
 * Side length of the HUD container.
 */
private val HudSize = 100.dp

/**
 * A loading HUD (Heads-Up Display) overlay.
 *
 * Displays a centred, rounded square containing an [ActivityIndicator]
 * of [ActivityIndicatorSize.Large] with an optional text label below it.
 * The background uses a dark semi-transparent fill with rounded corners,
 * providing an "ultra-thin material" appearance.
 *
 * @param modifier Modifier applied to the outer full-size container.
 * @param text Optional description shown below the spinner (e.g. "Loading…").
 * @param spinnerColor Tint color passed to the inner [ActivityIndicator].
 */
@Composable
fun LoadingHUD(
    modifier: Modifier = Modifier,
    text: String? = null,
    spinnerColor: Color = Color.White
) {
    val hudShape = remember { RoundedCornerShape(HudCornerRadius) }
    val hudBackground = Tokens.HudBackground.getThemedColor()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .size(HudSize)
                .graphicsLayer {
                    shadowElevation = 0f
                    clip = true
                    shape = hudShape
                }
                .clip(hudShape)
                .background(hudBackground),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ActivityIndicator(
                size = ActivityIndicatorSize.Large,
                color = spinnerColor
            )

            if (text != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Footnote(
                    text = text,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}

package com.chknkv.designsystem.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.button.ButtonCircle
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import org.jetbrains.compose.resources.DrawableResource
import anchor_app.core.coredesignsystem.generated.resources.Res
import anchor_app.core.coredesignsystem.generated.resources.ic_chevron_left
import com.chknkv.designsystem.Headline

/**
 * The primary screen scaffold for the application.
 *
 * It provides a unified structure including:
 * - A background with [containerColor].
 * - A custom top bar with a gradient blur effect that accommodates [title], [backButton], and [actionButton].
 * - Handling of system [WindowInsets] for status bars and navigation bars.
 * - Support for a floating action button through [floatingButton].
 *
 * @param modifier The modifier to be applied to the outer container.
 * @param containerColor The background color for the entire scaffold. Defaults to [Tokens.Background].
 * @param title Optional text to be displayed in the center of the top bar.
 * @param backButton Configuration for the leading (left) button in the top bar.
 * @param actionButton Configuration for the trailing (right) button in the top bar.
 * @param floatingButton Configuration for the floating action button.
 * @param floatingActionButtonPosition The position of the floating action button.
 * @param content The composable content of the screen. Receives [PaddingValues] to account for top and bottom bars.
 */
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    containerColor: Color = Tokens.Background.getThemedColor(),
    title: String? = null,
    backButton: ButtonConfig? = null,
    actionButton: ButtonConfig? = null,
    floatingButton: ButtonConfig? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit
) {
    val isEmptyTopBar = remember(title, backButton, actionButton) {
        title == null && backButton == null && actionButton == null
    }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val topBlurHeight = statusBarPadding + 72.dp
    val bottomBlurHeight = navBarPadding + 12.dp

    val emptyTopBarGradient = remember(containerColor) { containerColor.gradientForEmptyTopBar }
    val notEmptyTopBarGradient = remember(containerColor) { containerColor.gradientForNotEmptyTopBar }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = containerColor,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                if (floatingButton != null) {
                    ButtonCircle(
                        modifier = Modifier.padding(16.dp),
                        onClick = floatingButton.onClick,
                        buttonSize = 58.dp,
                        iconRes = floatingButton.iconRes,
                        iconColor = floatingButton.iconColor,
                        backgroundColor = floatingButton.backgroundColor,
                        hapticFeedbackType = floatingButton.hapticFeedbackType
                    )
                }
            },
            floatingActionButtonPosition = floatingActionButtonPosition
        ) { _ ->
            content(
                PaddingValues(
                    top = if (isEmptyTopBar) statusBarPadding + 16.dp else topBlurHeight,
                    bottom = bottomBlurHeight
                )
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(topBlurHeight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = if (isEmptyTopBar) emptyTopBarGradient else notEmptyTopBarGradient
                    )
            )
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                if (backButton != null) {
                    ButtonCircle(
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp),
                        onClick = backButton.onClick,
                        iconRes = backButton.iconRes,
                        iconColor = backButton.iconColor,
                        backgroundColor = backButton.backgroundColor,
                        hapticFeedbackType = backButton.hapticFeedbackType
                    )
                }

                if (title != null) {
                    Headline(
                        text = title,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                if (actionButton != null) {
                    ButtonCircle(
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp),
                        onClick = actionButton.onClick,
                        iconRes = actionButton.iconRes,
                        iconColor = actionButton.iconColor,
                        backgroundColor = actionButton.backgroundColor,
                        hapticFeedbackType = actionButton.hapticFeedbackType
                    )
                }
            }
        }
    }
}

/**
 * Gradient configuration for an empty top bar to provide a subtle fade.
 */
private val Color.gradientForEmptyTopBar
    get() = Brush.verticalGradient(
        0.00f to this.copy(alpha = 1.00f),
        0.30f to this.copy(alpha = 1.00f),
        0.45f to this.copy(alpha = 0.96f),
        0.60f to this.copy(alpha = 0.82f),
        0.72f to this.copy(alpha = 0.58f),
        0.82f to this.copy(alpha = 0.33f),
        0.90f to this.copy(alpha = 0.15f),
        0.96f to this.copy(alpha = 0.04f),
        1.00f to this.copy(alpha = 0.00f)
    )

/**
 * Gradient configuration for a top bar containing titles or buttons to provide a blur-like overlay.
 */
private val Color.gradientForNotEmptyTopBar
    get() = Brush.verticalGradient(
        0.00f to this.copy(alpha = 0.95f),
        0.55f to this.copy(alpha = 0.95f),
        0.65f to this.copy(alpha = 0.95f),
        0.72f to this.copy(alpha = 0.85f),
        0.78f to this.copy(alpha = 0.79f),
        0.84f to this.copy(alpha = 0.60f),
        0.89f to this.copy(alpha = 0.40f),
        0.93f to this.copy(alpha = 0.22f),
        0.97f to this.copy(alpha = 0.08f),
        1.00f to this.copy(alpha = 0.00f)
    )

/**
 * Configuration data for buttons used within [AppScaffold].
 *
 * @property onClick The callback to be invoked when the button is clicked.
 * @property iconRes The drawable resource for the button's icon. Defaults to a back arrow.
 * @property iconColor Optional tint color for the icon.
 * @property backgroundColor Optional background color for the button.
 * @property hapticFeedbackType Optional type of haptic feedback to trigger on click.
 */
data class ButtonConfig(
    val onClick: () -> Unit,
    val iconRes: DrawableResource = Res.drawable.ic_chevron_left,
    val iconColor: Color? = null,
    val backgroundColor: Color? = null,
    val hapticFeedbackType: HapticFeedbackType? = null
)

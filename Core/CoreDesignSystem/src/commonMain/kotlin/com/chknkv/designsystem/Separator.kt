package com.chknkv.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * A thin horizontal line used to visually separate content, typically between list items.
 *
 * Supports customizable insets and colors.
 *
 * @param modifier The modifier to be applied to the separator's layout.
 * @param leadingInset The indentation from the start (left) edge.
 * @param trailingInset The indentation from the end (right) edge.
 * @param color The color of the separator. Defaults to [Tokens.Separator] if unspecified.
 */
@Composable
fun Separator(
    modifier: Modifier = Modifier,
    leadingInset: Dp = 0.dp,
    trailingInset: Dp = 0.dp,
    color: Color = Color.Unspecified
) {
    val defaultColor = Tokens.Separator.getThemedColor()
    val targetColor = remember(color, defaultColor) {
        if (color == Color.Unspecified) defaultColor else color
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = leadingInset, end = trailingInset)
            .height(0.5.dp)
            .background(targetColor)
    )
}

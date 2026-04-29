package com.chknkv.designsystem.cell

import com.chknkv.designsystem.Separator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chknkv.designsystem.Body
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * A specialized list item component (Cell) designed for primary or warning actions.
 *
 * Applies specific action colors to the [title] based on the [isWarning] flag.
 *
 * @param title The action text to be displayed.
 * @param subtitle Optional supporting text displayed below the title.
 * @param isWarning If true, applies a warning color (e.g., Red) to the title. Defaults to false.
 * @param isDivider Whether to display a [Separator] at the bottom of the cell.
 * @param onClick Optional lambda to be invoked when the cell is clicked.
 */
@Composable
fun CellAction(
    title: String,
    subtitle: String? = null,
    isWarning: Boolean = false,
    isDivider: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val density = LocalDensity.current

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Column(
        modifier = clickableModifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = with(density) { 8.sp.toDp() })
            ) {
                Body(
                    text = title,
                    color = (if (isWarning) Tokens.Warning else Tokens.Action).getThemedColor(),
                    maxLines = 1
                )

                if (subtitle != null) {
                    Footnote(text = subtitle, isSecondary = true)
                }
            }
        }

        if (isDivider) {
            Separator()
        }
    }
}

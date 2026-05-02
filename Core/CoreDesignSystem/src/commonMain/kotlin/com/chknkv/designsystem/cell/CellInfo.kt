package com.chknkv.designsystem.cell

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Body
import com.chknkv.designsystem.SquareIcon
import com.chknkv.designsystem.Title3
import com.chknkv.designsystem.module.Module
import org.jetbrains.compose.resources.DrawableResource

/**
 * An information card component (Cell) used to display prominent content with a large icon.
 *
 * It wraps the content in a [Module] and provides a specialized layout for titles and icons.
 *
 * @param title The main headline text (uses [Title3]).
 * @param subtitle Optional supporting text displayed below the title (uses [Body]).
 * @param iconRes Optional large icon resource to be displayed at the top.
 * @param iconColor Optional background color for the icon.
 * @param iconGradient Optional background gradient for the icon.
 * @param outPaddingValues External padding applied around the entire module.
 * @param innerPaddingValues Paddings applied inside the module container.
 * @param onClick Optional lambda to be invoked when the card is clicked.
 */
@Composable
fun CellInfo(
    title: String,
    subtitle: String? = null,
    iconRes: DrawableResource? = null,
    iconColor: Color? = null,
    iconGradient: Brush? = null,
    outPaddingValues: PaddingValues = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 0.dp),
    innerPaddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    onClick: (() -> Unit)? = null
) {
    Module(
        innerPaddingValues = innerPaddingValues,
        outPaddingValues = outPaddingValues
    ) {
        val clickableModifier = if (onClick != null) {
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        } else {
            Modifier
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(clickableModifier)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    if (iconRes != null) {
                        Box(
                            modifier = Modifier.size(48.dp).padding(bottom = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            SquareIcon(
                                iconRes = iconRes,
                                iconSize = 48.dp,
                                iconResSize = 32.dp,
                                backgroundColor = iconColor,
                                backgroundGradient = iconGradient
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Title3(text = title, maxLines = 1)
                    if (subtitle != null) {
                        Body(
                            modifier = Modifier.padding(top = 4.dp),
                            text = subtitle,
                            isSecondary = true
                        )
                    }
                }
            }
        }
    }
}

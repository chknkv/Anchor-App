package com.chknkv.designsystem.cell

import com.chknkv.designsystem.Separator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chknkv.designsystem.Body
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.SquareIcon
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import anchor_app.core.coredesignsystem.generated.resources.Res
import anchor_app.core.coredesignsystem.generated.resources.ic_chevron_right

/**
 * Универсальный компонент элемента списка (ячейка), соответствующий стандартам дизайн-системы.
 * 
 * Поддерживает опциональную иконку, заголовок, подзаголовок, произвольный контент в конце 
 * и индикатор перехода (chevron).
 * 
 * @param title Основной текст ячейки.
 * @param subtitle Опциональный пояснительный текст под заголовком.
 * @param iconRes Опциональный ресурс иконки в начале ячейки.
 * @param iconColor Опциональный цвет фона иконки.
 * @param iconGradient Опциональный градиентный фон иконки.
 * @param isChevron Флаг отображения стрелки перехода в конце ячейки.
 * @param isDivider Флаг отображения [Separator] под ячейкой.
 * @param trailingContent Опциональный компонент, отображаемый в конце ячейки.
 * @param onClick Коллбэк, вызываемый при нажатии на ячейку.
 */
@Composable
fun Cell(
    title: String,
    subtitle: String? = null,
    iconRes: DrawableResource? = null,
    iconColor: Color? = null,
    iconGradient: Brush? = null,
    isChevron: Boolean = false,
    isDivider: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
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
            if (iconRes != null) {
                Box(
                    modifier = Modifier.size(34.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SquareIcon(
                        iconRes = iconRes,
                        backgroundColor = iconColor,
                        backgroundGradient = iconGradient
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            } else {
                Spacer(modifier = Modifier.width(4.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = with(density) { 8.sp.toDp() })
            ) {
                Body(text = title, maxLines = 1)
                if (subtitle != null) {
                    Footnote(text = subtitle, isSecondary = true)
                }
            }

            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingContent()
            }

            if (isChevron) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    modifier = Modifier.size(with(density) { 20.sp.toDp() }),
                    painter = painterResource(Res.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = Tokens.IconSecondary.getThemedColor()
                )
            }
        }

        if (isDivider) {
            val dividerIndent = remember(iconRes) {
                if (iconRes != null) 46.dp else 4.dp 
            }
            Separator(leadingInset = dividerIndent)
        }
    }
}

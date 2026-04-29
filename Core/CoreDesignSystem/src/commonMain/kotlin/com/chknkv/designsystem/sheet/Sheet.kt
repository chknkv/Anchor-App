package com.chknkv.designsystem.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Subheadline
import com.chknkv.designsystem.Title3
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * Кастомный модальный компонент "шторка" (Bottom Sheet).
 * 
 * Позволяет отображать контент, выезжающий снизу экрана. Поддерживает заголовок,
 * подзаголовок, кнопку действия в шапке и управление поведением жестов (drag-to-dismiss).
 * 
 * @param isVisible Флаг видимости шторки.
 * @param onDismissRequest Коллбэк для закрытия шторки.
 * @param modifier Модификатор макета.
 * @param title Заголовок шторки.
 * @param subtitle Подзаголовок шторки.
 * @param actionButton Опциональный компонент кнопки в правой части шапки.
 * @param isDragable Разрешено ли закрытие шторки свайпом вниз.
 * @param onDragDismissAction Коллбэк при закрытии шторки свайпом.
 * @param isOutsideClickEnabled Разрешено ли закрытие шторки кликом по затемненной области (scrim).
 * @param onOutsideClickAction Коллбэк при клике по затемненной области.
 * @param heightBehavior Настройка высоты шторки ([SheetHeightBehavior]).
 * @param content Содержимое шторки.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    actionButton: (@Composable () -> Unit)? = null,
    isDragable: Boolean = false,
    onDragDismissAction: (() -> Unit)? = null,
    isOutsideClickEnabled: Boolean = false,
    onOutsideClickAction: (() -> Unit)? = null,
    heightBehavior: SheetHeightBehavior = SheetHeightBehavior.WrapContent,
    content: @Composable ColumnScope.() -> Unit
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            if (!isDragable && newValue == SheetValue.Hidden) {
                false
            } else {
                if (newValue == SheetValue.Hidden) {
                    onDragDismissAction?.invoke()
                }
                true
            }
        }
    )

    ModalBottomSheet(
        onDismissRequest = {
            if (isOutsideClickEnabled) {
                onOutsideClickAction?.invoke()
                onDismissRequest()
            }
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Tokens.BackgroundSheet.getThemedColor(),
        dragHandle = null,
        scrimColor = Color.Black.copy(alpha = 0.4f),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    when (heightBehavior) {
                        is SheetHeightBehavior.Fraction -> Modifier.fillMaxHeight(heightBehavior.ratio)
                        is SheetHeightBehavior.WrapContent -> Modifier
                    }
                )
        ) {
            if (isDragable) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 36.dp, height = 5.dp)
                            .clip(RoundedCornerShape(2.5.dp))
                            .background(Tokens.Separator.getThemedColor())
                    )
                }
            }

            if (title != null || subtitle != null || actionButton != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (title != null) {
                            Title3(
                                text = title,
                                maxLines = 3
                            )
                        }

                        if (subtitle != null) {
                            Subheadline(
                                text = subtitle,
                                maxLines = 5,
                                isSecondary = true,
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            )
                        }
                    }

                    Box(modifier = Modifier.width(48.dp)) { actionButton?.invoke() }
                }

            }

            content()

            Spacer(modifier = Modifier.fillMaxWidth().height(16.dp))
        }
    }
}

/**
 * Определяет поведение высоты компонента [Sheet].
 */
sealed interface SheetHeightBehavior {

    /** Высота подстраивается под содержимое. */
    data object WrapContent : SheetHeightBehavior

    /** Высота занимает фиксированную долю экрана. */
    data class Fraction(val ratio: Float) : SheetHeightBehavior
}
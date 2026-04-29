package com.chknkv.corepasscode.presentation.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chknkv.corepasscode.presentation.alert.PasscodeAlertKind.Destructive
import com.chknkv.corepasscode.presentation.alert.PasscodeAlertKind.Neutral
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.sheet.Sheet
import com.chknkv.designsystem.sheet.SheetHeightBehavior

/**
 * Двухкнопочный alert-подтверждение в стиле bottom sheet.
 *
 * Используется для подтверждения пропуска создания passcode и сброса passcode.
 * Positive-кнопка — деструктивное действие, negative — отмена.
 *
 * @param isVisible Показывать ли alert.
 * @param title Заголовок.
 * @param subtitle Подзаголовок.
 * @param positiveText Текст деструктивной кнопки.
 * @param negativeText Текст кнопки отмены.
 * @param positiveKind Стиль деструктивной кнопки (Warning/Neutral).
 * @param onPositive Колбэк деструктивного действия.
 * @param onNegative Колбэк отмены (закрытие).
 */
@Composable
fun PasscodeAlert(
    isVisible: Boolean,
    title: String,
    subtitle: String,
    positiveText: String,
    negativeText: String,
    positiveKind: PasscodeAlertKind = Destructive,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
) {
    Sheet(
        isVisible = isVisible,
        isDragable = true,
        title = title,
        subtitle = subtitle,
        onDismissRequest = onNegative,
        isOutsideClickEnabled = true,
        heightBehavior = SheetHeightBehavior.WrapContent,
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                modifier = Modifier.weight(1f),
                text = negativeText,
                style = ButtonStyle.Default,
                onClick = onNegative,
            )
            Button(
                modifier = Modifier.weight(1f),
                text = positiveText,
                style = when (positiveKind) {
                    Destructive -> ButtonStyle.Warning
                    Neutral -> ButtonStyle.Action
                },
                onClick = onPositive,
            )
        }
    }
}

/**
 * Стиль положительной кнопки алерта.
 */
enum class PasscodeAlertKind { Destructive, Neutral }

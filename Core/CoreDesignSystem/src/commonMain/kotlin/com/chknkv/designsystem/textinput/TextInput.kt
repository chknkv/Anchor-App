package com.chknkv.designsystem.textinput

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chknkv.designsystem.Separator
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import anchor_app.core.coredesignsystem.generated.resources.Res
import anchor_app.core.coredesignsystem.generated.resources.ic_chevron_right
import anchor_app.core.coredesignsystem.generated.resources.ic_cross
import anchor_app.core.coredesignsystem.generated.resources.text_input_action_content_description
import anchor_app.core.coredesignsystem.generated.resources.text_input_clear_content_description
import anchor_app.core.coredesignsystem.generated.resources.text_input_hide_password
import anchor_app.core.coredesignsystem.generated.resources.text_input_show_password

/**
 * Настраиваемый компонент текстового ввода на базе [BasicTextField].
 * 
 * Поддерживает иконки в начале и конце строки, переключение видимости пароля, 
 * многострочный ввод и опциональный разделитель снизу.
 * 
 * @param value Текущее текстовое значение.
 * @param onValueChange Коллбэк, вызываемый при изменении текста.
 * @param placeholder Текст подсказки (hint), когда поле пустое.
 * @param modifier Модификатор макета.
 * @param maxLines Максимальное количество строк. Если [Int.MAX_VALUE], поле переходит 
 * в режим фиксированной высоты (8 строк).
 * @param keyboardOptions Настройки программной клавиатуры.
 * @param keyboardActions Действия программной клавиатуры.
 * @param isPassword Флаг скрытия вводимых символов (для паролей).
 * @param leadingIcon Опциональный компонент в начале поля.
 * @param trailingIconMode Режим отображения иконки в конце поля ([TrailingIconMode]).
 * @param actionColor Цвет интерактивных элементов (кнопка "Показать", иконка действия).
 * @param isDivider Флаг отображения [Separator] под полем ввода.
 */
@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isPassword: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIconMode: TrailingIconMode = TrailingIconMode.Clear,
    actionColor: Color = Tokens.Action.getThemedColor(),
    isDivider: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val primaryTextColor = Tokens.TextPrimary.getThemedColor()
    val secondaryTextColor = Tokens.TextSecondary.getThemedColor()
    val accentColor = Tokens.Action.getThemedColor()

    val bodyStyle = remember(primaryTextColor) {
        TextStyle(
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.Default,
            letterSpacing = (-0.41).sp,
            color = primaryTextColor
        )
    }

    val placeholderStyle = remember(bodyStyle, secondaryTextColor) {
        bodyStyle.copy(color = secondaryTextColor)
    }

    val visualTransformation = remember(isPassword, passwordVisible) {
        if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        }
    }

    val isFixedMode = maxLines == Int.MAX_VALUE
    val minLines = if (isFixedMode) 8 else 1
    val resolvedMaxLines = if (isFixedMode) 8 else maxLines

    val textFieldModifier = remember(isFixedMode, modifier) {
        if (isFixedMode) {
            modifier
        } else {
            modifier
                .heightIn(min = 50.dp)
                .animateContentSize()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = textFieldModifier.fillMaxWidth(),
            textStyle = bodyStyle,
            cursorBrush = SolidColor(accentColor),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = remember { MutableInteractionSource() },
            minLines = minLines,
            maxLines = resolvedMaxLines,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = if (resolvedMaxLines > 1) Alignment.Top else Alignment.CenterVertically
                ) {
                    val contentPaddingModifier = if (resolvedMaxLines > 1) {
                        Modifier.padding(vertical = 18.dp)
                    } else {
                        Modifier.heightIn(min = 50.dp)
                    }

                    Row(
                        modifier = contentPaddingModifier.weight(1f),
                        verticalAlignment = if (resolvedMaxLines > 1) Alignment.Top else Alignment.CenterVertically
                    ) {
                        if (leadingIcon != null) {
                            Box(modifier = Modifier.padding(end = 12.dp).then(if (resolvedMaxLines == 1) Modifier.align(Alignment.CenterVertically) else Modifier)) {
                                leadingIcon()
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .then(if (resolvedMaxLines == 1) Modifier.align(Alignment.CenterVertically) else Modifier),
                            contentAlignment = if (resolvedMaxLines == 1) Alignment.CenterStart else Alignment.TopStart
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = placeholderStyle,
                                    maxLines = resolvedMaxLines
                                )
                            }
                            innerTextField()
                        }
                    }

                    if (value.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .padding(vertical = if (resolvedMaxLines > 1) 18.dp else 0.dp)
                                .then(if (resolvedMaxLines == 1) Modifier.align(Alignment.CenterVertically) else Modifier),
                            contentAlignment = Alignment.Center
                        ) {
                            TrailingIcon(
                                mode = trailingIconMode,
                                actionColor = actionColor,
                                onValueChange = onValueChange,
                                isPassword = isPassword,
                                passwordVisible = passwordVisible,
                                onPasswordVisibleToggle = { passwordVisible = !passwordVisible }
                            )
                        }
                    }
                }
            }
        )

        if (isDivider) {
            Separator(
                modifier = Modifier.padding(top = 0.dp),
                leadingInset = if (leadingIcon != null) 32.dp else 4.dp
            )
        }
    }
}

/**
 * Режимы отображения и поведения иконки в конце [TextInput].
 */
sealed interface TrailingIconMode {
    /** Иконка отсутствует. */
    data object None : TrailingIconMode
    /** Иконка очистки (крестик), сбрасывающая значение поля. */
    data object Clear : TrailingIconMode
    /** Иконка-стрелка с кастомным действием при нажатии. */
    data class Action(val onClick: () -> Unit) : TrailingIconMode
    /** Текстовая кнопка переключения видимости пароля. */
    data object ShowPassword : TrailingIconMode
}

@Composable
private fun TrailingIcon(
    mode: TrailingIconMode,
    actionColor: Color,
    onValueChange: (String) -> Unit,
    isPassword: Boolean,
    passwordVisible: Boolean,
    onPasswordVisibleToggle: () -> Unit
) {
    val iconSecondaryColor = Tokens.IconSecondary.getThemedColor()

    when (mode) {
        TrailingIconMode.None -> { /* Nothing to show */ }

        TrailingIconMode.Clear -> {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = iconSecondaryColor.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onValueChange("") }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_cross),
                    contentDescription = stringResource(Res.string.text_input_clear_content_description),
                    tint = iconSecondaryColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        is TrailingIconMode.Action -> {
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = mode.onClick
                    )
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_chevron_right),
                    contentDescription = stringResource(Res.string.text_input_action_content_description),
                    tint = actionColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        TrailingIconMode.ShowPassword -> {
            if (isPassword) {
                Text(
                    text = stringResource(
                        if (passwordVisible) Res.string.text_input_hide_password 
                        else Res.string.text_input_show_password
                    ),
                    color = actionColor,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onPasswordVisibleToggle
                    )
                )
            }
        }
    }
}

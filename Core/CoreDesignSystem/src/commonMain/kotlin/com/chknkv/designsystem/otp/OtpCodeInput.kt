package com.chknkv.designsystem.otp

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import kotlinx.coroutines.delay

/**
 * Компонент ввода OTP-кода (или PIN-кода).
 * 
 * Отображает набор отдельных ячеек для каждой цифры. Поддерживает анимацию "встряски" при ошибке,
 * состояние загрузки (волнообразная анимация ячеек) и автоматический фокус.
 * 
 * @param value Текущее значение кода.
 * @param onValueChange Коллбэк при изменении кода.
 * @param modifier Модификатор макета.
 * @param length Количество цифр в коде.
 * @param state Текущее состояние ввода ([PinInputState]).
 * @param onSuccessAction Коллбэк, вызываемый автоматически при вводе всех цифр.
 * @param autoFocus Флаг автоматического запроса фокуса при появлении.
 * @param colors Цветовая схема компонента.
 */
@Composable
fun OtpCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 4,
    state: PinInputState = PinInputState.Input,
    onSuccessAction: (String) -> Unit = {},
    autoFocus: Boolean = true,
    colors: PinInputColors = PinInputDefaults.colors()
) {
    val focusRequester = remember { FocusRequester() }
    val haptic = LocalHapticFeedback.current

    var tfv by remember {
        mutableStateOf(TextFieldValue(value, TextRange(value.length)))
    }

    var lastEmittedValue by remember { mutableStateOf(value) }

    LaunchedEffect(value) {
        if (value != lastEmittedValue) {
            tfv = tfv.copy(text = value, selection = TextRange(value.length))
            lastEmittedValue = value
        }
    }

    LaunchedEffect(Unit) {
        if (autoFocus) {
            delay(500)
            focusRequester.requestFocus()
        }
    }

    var previousLength by remember { mutableStateOf(value.length) }
    LaunchedEffect(value) {
        if (value.length == length && previousLength < length) {
            onSuccessAction(value)
        }
        previousLength = value.length
    }

    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(state) {
        if (state is PinInputState.Error) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    (-10f) at 50
                    10f at 100
                    (-10f) at 150
                    10f at 200
                    (-10f) at 250
                    10f at 300
                    0f at 400
                }
            )
        }
    }

    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            focusRequester.requestFocus()
        },
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = tfv,
            onValueChange = { newTfv ->
                if (state is PinInputState.Loading) return@BasicTextField

                val newDigits = newTfv.text.filter { it.isDigit() }
                
                if (newDigits.length > length && newTfv.text.length > tfv.text.length) {
                    tfv = tfv.copy(selection = TextRange(tfv.text.length))
                    return@BasicTextField
                }
                
                val digits = newDigits.take(length)

                if (digits == tfv.text) {
                    if (newTfv.selection.start != digits.length) {
                        tfv = newTfv.copy(selection = TextRange(digits.length))
                    }
                    return@BasicTextField
                }

                if (digits.length > tfv.text.length) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }

                val updatedTfv = newTfv.copy(
                    text = digits,
                    selection = TextRange(digits.length)
                )
                
                tfv = updatedTfv
                lastEmittedValue = digits
                onValueChange(digits)
            },
            modifier = Modifier
                .size(1.dp)
                .alpha(0f)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                autoCorrectEnabled = false
            )
        )

        Row(
            modifier = Modifier.graphicsLayer { translationX = shakeOffset.value },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(length) { index ->
                val char = value.getOrNull(index)?.toString() ?: ""
                val isFocused = value.length == index && state !is PinInputState.Loading

                PinCell(
                    char = char,
                    isFocused = isFocused,
                    state = state,
                    colors = colors,
                    index = index
                )
            }
        }
    }
}

@Composable
private fun PinCell(
    char: String,
    isFocused: Boolean,
    state: PinInputState,
    colors: PinInputColors,
    index: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveTransition")
    
    val waveOffset by if (state is PinInputState.Loading) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 600
                    0f at 0
                    (-16f) at 300 using LinearOutSlowInEasing
                    0f at 600 using FastOutLinearInEasing
                },
                initialStartOffset = StartOffset(index * 100)
            ),
            label = "WaveOffset"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val borderColor = when {
        state is PinInputState.Error -> colors.errorColor
        isFocused -> colors.focusedBorder
        else -> colors.unfocusedBorder
    }

    val textColor = if (state is PinInputState.Error) colors.errorColor else colors.cellText

    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 56.dp)
            .graphicsLayer { translationY = waveOffset }
            .clip(RoundedCornerShape(8.dp))
            .background(colors.cellBackground)
            .border(
                width = if (isFocused || state is PinInputState.Error) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = TextStyle(
                fontSize = 24.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = textColor
            )
        )
    }
}

/**
 * Состояния компонента ввода кода.
 */
sealed interface PinInputState {
    /** Режим ввода. */
    data object Input : PinInputState
    /** Режим проверки (блокирует ввод, показывает анимацию загрузки). */
    data object Loading : PinInputState
    /** Режим ошибки (подсвечивает красным, запускает "встряску"). */
    data object Error : PinInputState
}

/**
 * Цветовая схема для компонента [OtpCodeInput].
 */
@Immutable
data class PinInputColors(
    val cellBackground: Color,
    val cellText: Color,
    val errorColor: Color,
    val focusedBorder: Color,
    val unfocusedBorder: Color
)

object PinInputDefaults {
    @Composable
    fun colors(
        cellBackground: Color = Tokens.Module.getThemedColor(),
        cellText: Color = Tokens.TextPrimary.getThemedColor(),
        errorColor: Color = Tokens.Warning.getThemedColor(),
        focusedBorder: Color = Tokens.Action.getThemedColor(),
        unfocusedBorder: Color = Tokens.Separator.getThemedColor()
    ): PinInputColors = PinInputColors(
        cellBackground = cellBackground,
        cellText = cellText,
        errorColor = errorColor,
        focusedBorder = focusedBorder,
        unfocusedBorder = unfocusedBorder
    )
}
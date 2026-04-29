package com.chknkv.designsystem.passcode

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import org.jetbrains.compose.resources.painterResource
import anchor_app.core.coredesignsystem.generated.resources.Res
import anchor_app.core.coredesignsystem.generated.resources.ic_backspace

private val KEY_SIZE = 82.dp
private val DIGIT_ROWS = listOf(
    listOf(1, 2, 3),
    listOf(4, 5, 6),
    listOf(7, 8, 9),
)

/**
 * Клавиатура для ввода пароля (Passcode).
 * 
 * Состоит из цифровых клавиш (0-9) и функциональной клавиши (удаление или биометрия).
 * Клавиши имеют эффект "стеклянного" фона, анимацию нажатия и звуковое сопровождение.
 * 
 * @param onDigitClick Коллбэк при нажатии на цифру.
 * @param onDeleteClick Коллбэк при нажатии на кнопку удаления.
 * @param onBiometryClick Коллбэк при нажатии на иконку биометрии.
 * @param biometryPainter Иконка биометрии (Face ID / Touch ID / Fingerprint).
 * @param filledCount Количество уже введенных цифр (влияет на отображение кнопки удаления/биометрии).
 * @param modifier Модификатор макета.
 */
@Composable
fun PasscodeKeyboard(
    onDigitClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onBiometryClick: (() -> Unit)? = null,
    biometryPainter: Painter? = null,
    filledCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val showBiometry = onBiometryClick != null && biometryPainter != null && filledCount == 0

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        DIGIT_ROWS.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
            ) {
                row.forEach { digit ->
                    PasscodeDigitKey(
                        digit = digit,
                        onClick = { onDigitClick(digit) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(KEY_SIZE))

            PasscodeDigitKey(digit = 0, onClick = { onDigitClick(0) })

            when {
                showBiometry -> PasscodeActionKey(
                    painter = biometryPainter,
                    onClick = { onBiometryClick.invoke() }
                )
                filledCount > 0 -> PasscodeActionKey(
                    painter = painterResource(Res.drawable.ic_backspace),
                    onClick = onDeleteClick
                )
                else -> Spacer(modifier = Modifier.size(KEY_SIZE))
            }
        }
    }
}

@Composable
private fun PasscodeDigitKey(
    digit: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val playKeyClick = rememberKeyClickPlayer()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.87f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "scale"
    )

    val glassColor = Tokens.PasscodeKeyGlass.getThemedColor()

    Box(
        modifier = modifier
            .size(KEY_SIZE)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(color = glassColor, shape = CircleShape)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val press = PressInteraction.Press(offset)
                        interactionSource.emit(press)
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        playKeyClick()
                        tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(press))
                    },
                    onTap = { onClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit.toString(),
            fontSize = 30.sp,
            fontWeight = FontWeight.Normal,
            color = Tokens.TextPrimary.getThemedColor()
        )
    }
}

@Composable
private fun PasscodeActionKey(
    painter: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val playKeyClick = rememberKeyClickPlayer()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.87f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.5f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "alpha"
    )

    val glassColor = Tokens.PasscodeKeyGlass.getThemedColor()

    Box(
        modifier = modifier
            .size(KEY_SIZE)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .background(color = glassColor, shape = CircleShape)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val press = PressInteraction.Press(offset)
                        interactionSource.emit(press)
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        playKeyClick()
                        tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(press))
                    },
                    onTap = { onClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            painter = painter,
            contentDescription = null,
            tint = Tokens.Action.getThemedColor()
        )
    }
}

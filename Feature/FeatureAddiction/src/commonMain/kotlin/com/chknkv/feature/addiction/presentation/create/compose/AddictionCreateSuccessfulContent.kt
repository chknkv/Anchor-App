package com.chknkv.feature.addiction.presentation.create.compose

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addictionCreate_category_section_label
import anchor_app.feature.featureaddiction.generated.resources.addictionCreate_categoryEmpty_error
import anchor_app.feature.featureaddiction.generated.resources.addictionCreate_description_hint
import anchor_app.feature.featureaddiction.generated.resources.addictionCreate_gradient_section_label
import anchor_app.feature.featureaddiction.generated.resources.addictionCreate_icon_section_label
import anchor_app.feature.featureaddiction.generated.resources.addictionCreate_name_hint
import anchor_app.feature.featureaddiction.generated.resources.addictionCreate_nameEmpty_error
import anchor_app.feature.featureaddiction.generated.resources.addictionCreate_submit_button
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.chip.Chip
import com.chknkv.designsystem.module.Module
import com.chknkv.designsystem.module.ModuleContent
import com.chknkv.designsystem.textinput.TextInput
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import com.chknkv.feature.addiction.models.presentation.all.AddictionCategoryUi
import com.chknkv.feature.addiction.models.presentation.create.AddictionCreateUiAction
import com.chknkv.feature.addiction.models.presentation.create.AddictionCreateUiResult
import com.chknkv.feature.addiction.presentation.toTitleStringResource
import com.chknkv.feature.addiction.presentation.toGradientBrush
import com.chknkv.feature.addiction.presentation.toGradientPrimaryColor
import com.chknkv.feature.addiction.presentation.toIconDrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Успешное состояние экрана создания привычки — форма с полями ввода и пикерами.
 *
 * Разбита на секции: поля текста, выбор иконки, выбор цвета, выбор категории,
 * кнопка создания и сообщение об ошибке.
 *
 * @param result Текущее состояние формы.
 * @param onAction Обработчик действий пользователя.
 * @param contentPadding Отступы для корректного отображения под blur-зоной AppScaffold.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AddictionCreateSuccessfulContent(
    result: AddictionCreateUiResult,
    onAction: (AddictionCreateUiAction) -> Unit,
    contentPadding: PaddingValues,
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val emptyTitleError = stringResource(Res.string.addictionCreate_nameEmpty_error)
    val emptyCategoryError = stringResource(Res.string.addictionCreate_categoryEmpty_error)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(contentPadding)
            .imePadding()
            .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
    ) {
        AddictionCreateFieldsSection(result = result, onAction = onAction, focusManager = focusManager)
        AddictionCreateIconSection(result = result, onAction = onAction)
        AddictionCreateGradientSection(result = result, onAction = onAction)
        AddictionCreateCategorySection(result = result, onAction = onAction)
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))
        AddictionCreateSubmitSection(
            result = result,
            onAction = onAction,
            emptyTitleError = emptyTitleError,
            emptyCategoryError = emptyCategoryError,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ——————————————————————————————————————————
// Private sections
// ——————————————————————————————————————————

/**
 * Секция текстовых полей для ввода названия и описания привычки.
 * Включает в себя валидацию фокуса и переходы клавиатуры (Next/Done).
 *
 * @param result Результат состояния, содержащий текущие значения названия и описания.
 * @param onAction Обработчик изменения текста в полях.
 * @param focusManager Менеджер фокуса для управления переходами между полями.
 */
@Composable
internal fun AddictionCreateFieldsSection(
    result: AddictionCreateUiResult,
    onAction: (AddictionCreateUiAction) -> Unit,
    focusManager: FocusManager,
) {
    Module(outPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp)) {
        Column {
            TextInput(
                value = result.title,
                onValueChange = remember(onAction) {
                    { onAction(AddictionCreateUiAction.ChangeTitle(it)) }
                },
                placeholder = stringResource(Res.string.addictionCreate_name_hint),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                isDivider = true,
            )
            TextInput(
                value = result.description,
                onValueChange = remember(onAction) {
                    { onAction(AddictionCreateUiAction.ChangeDescription(it)) }
                },
                placeholder = stringResource(Res.string.addictionCreate_description_hint),
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                isDivider = false,
            )
        }
    }
}

/**
 * Секция выбора визуальной иконки для новой привычки.
 * Содержит заголовок и сетку ([FlowRow]) с доступными иконками.
 *
 * @param result Результат состояния, содержащий доступные ключи иконок и текущий выбранный ключ.
 * @param onAction Обработчик выбора иконки.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AddictionCreateIconSection(
    result: AddictionCreateUiResult,
    onAction: (AddictionCreateUiAction) -> Unit,
) {
    ModuleContent(
        title = stringResource(Res.string.addictionCreate_icon_section_label),
        innerPaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            result.availableIconKeys.forEach { iconKey ->
                val isSelected = iconKey == result.selectedIconKey
                IconPickerItem(
                    iconKey = iconKey,
                    isSelected = isSelected,
                    onSelect = remember(onAction, iconKey) {
                        { onAction(AddictionCreateUiAction.SelectIcon(iconKey)) }
                    }
                )
            }
        }
    }
}

/**
 * Секция выбора цветового градиента (фона иконки).
 * Содержит заголовок и сетку ([FlowRow]) с доступными градиентами.
 *
 * @param result Состояние формы.
 * @param onAction Обработчик действий.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AddictionCreateGradientSection(
    result: AddictionCreateUiResult,
    onAction: (AddictionCreateUiAction) -> Unit,
) {
    ModuleContent(
        title = stringResource(Res.string.addictionCreate_gradient_section_label),
        innerPaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            result.availableGradientKeys.forEach { gradientKey ->
                val isSelected = gradientKey == result.selectedGradientKey
                GradientPickerItem(
                    gradientKey = gradientKey,
                    isSelected = isSelected,
                    onSelect = remember(onAction, gradientKey) {
                        { onAction(AddictionCreateUiAction.SelectGradient(gradientKey)) }
                    },
                )
            }
        }
    }
}

/**
 * Секция выбора категории привычки (Lifestyle, Health, Sport и т.д.).
 * Отображает горизонтальный ряд чипсов ([Chip]) для выбора одной из доступных категорий.
 *
 * @param result Результат состояния, содержащий список доступных категорий и выбранную категорию.
 * @param onAction Обработчик выбора категории.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AddictionCreateCategorySection(
    result: AddictionCreateUiResult,
    onAction: (AddictionCreateUiAction) -> Unit,
) {
    ModuleContent(
        title = stringResource(Res.string.addictionCreate_category_section_label),
        innerPaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    ) {
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            AddictionCategoryUi.entries.forEach { option ->
                Chip(
                    text = stringResource(option.toTitleStringResource()),
                    isSelected = option == result.selectedCategory,
                    onActionHandler = remember(onAction, option) {
                        { onAction(AddictionCreateUiAction.SelectCategory(option)) }
                    },
                )
            }
        }
    }
}

/**
 * Секция с кнопкой подтверждения и отображением ошибок валидации.
 *
 * @param result Текущий результат состояния создания привычки (текст ошибок, флаг загрузки).
 * @param onAction Обработчик отправки формы.
 * @param emptyTitleError Локализованный текст ошибки пустого названия.
 * @param emptyCategoryError Локализованный текст ошибки невыбранной категории.
 */
@Composable
internal fun AddictionCreateSubmitSection(
    result: AddictionCreateUiResult,
    onAction: (AddictionCreateUiAction) -> Unit,
    emptyTitleError: String,
    emptyCategoryError: String,
) {
    val warningColor = Tokens.Warning.getThemedColor()

    AnimatedVisibility(
        visible = result.errorMessage != null,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
    ) {
        Footnote(
            text = result.errorMessage ?: "",
            color = warningColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 6.dp),
        )
    }

    Button(
        text = stringResource(Res.string.addictionCreate_submit_button),
        style = ButtonStyle.Action,
        enabled = !result.isLoading,
        onClick = remember(onAction, emptyTitleError, emptyCategoryError) {
            { onAction(AddictionCreateUiAction.Submit(emptyTitleError, emptyCategoryError)) }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    )
}

// ——————————————————————————————————————————
// Internal sub-composables
// ——————————————————————————————————————————

/**
 * Элемент выбора иконки для привычки.
 *
 * @param iconKey Строковый ключ иконки.
 * @param isSelected Флаг, указывающий, выбрана ли данная иконка в текущий момент.
 * @param onSelect Обработчик клика по элементу.
 */
@Composable
internal fun IconPickerItem(
    iconKey: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    var isInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(isSelected) {
        if (isInitialized) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } else {
            isInitialized = true
        }
    }

    val bgColor = Tokens.ChipBackgroundUnselected.getThemedColor()
    val iconTint = Tokens.ChipContentUnselected.getThemedColor()
    val borderColor = Tokens.ChipContentUnselected.getThemedColor().copy(alpha = 0.3f)

    Box(modifier = Modifier.size(56.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(bgColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSelect,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconKey.toIconDrawableResource()),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = iconTint,
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(width = 2.dp, color = borderColor, shape = CircleShape),
            )
        }
    }
}

/**
 * Элемент выбора градиента для иконки привычки.
 *
 * @param gradientKey Строковый ключ градиента (например, "blue", "green").
 * @param isSelected Флаг, указывающий, выбран ли данный градиент в текущий момент.
 * @param onSelect Обработчик клика по элементу.
 */
@Composable
internal fun GradientPickerItem(
    gradientKey: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    var isInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(isSelected) {
        if (isInitialized) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } else {
            isInitialized = true
        }
    }

    val brush = remember(gradientKey) { gradientKey.toGradientBrush() }
    val borderColor = gradientKey.toGradientPrimaryColor()

    Box(modifier = Modifier.size(56.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.7f }
                .clip(CircleShape)
                .background(brush = brush)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSelect,
                ),
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(width = 2.dp, color = borderColor, shape = CircleShape),
            )
        }
    }
}

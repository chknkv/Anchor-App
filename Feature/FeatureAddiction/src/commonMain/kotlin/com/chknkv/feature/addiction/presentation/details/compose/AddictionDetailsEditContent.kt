package com.chknkv.feature.addiction.presentation.details.compose

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addictionDetails_categoryEmpty_error
import anchor_app.feature.featureaddiction.generated.resources.addictionDetails_edit_button
import anchor_app.feature.featureaddiction.generated.resources.addictionDetails_nameEmpty_error
import anchor_app.feature.featureaddiction.generated.resources.addiction_common_error_generic
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import com.chknkv.feature.addiction.models.presentation.create.AddictionCreateUiAction
import com.chknkv.feature.addiction.models.presentation.create.AddictionCreateUiResult
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiResult
import com.chknkv.feature.addiction.presentation.create.compose.AddictionCreateCategorySection
import com.chknkv.feature.addiction.presentation.create.compose.AddictionCreateFieldsSection
import com.chknkv.feature.addiction.presentation.create.compose.AddictionCreateGradientSection
import com.chknkv.feature.addiction.presentation.create.compose.AddictionCreateIconSection
import org.jetbrains.compose.resources.stringResource

/**
 * Контент экрана деталей привычки в режиме редактирования.
 * Позволяет изменять параметры существующей привычки (название, иконку, градиент).
 *
 * @param result Результат состояния с данными редактируемой привычки.
 * @param onAction Обработчик действий редактирования.
 * @param contentPadding Отступы, передаваемые от Scaffold (учитывают безопасные зоны и топбар).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AddictionDetailsEditContent(
    result: AddictionDetailsUiResult,
    onAction: (AddictionDetailsUiAction) -> Unit,
    contentPadding: PaddingValues,
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val emptyTitleError = stringResource(Res.string.addictionDetails_nameEmpty_error)
    val emptyCategoryError = stringResource(Res.string.addictionDetails_categoryEmpty_error)

    val editResult = remember(
        result.editTitle,
        result.editDescription,
        result.editIcon,
        result.editGradient,
        result.editCategory,
        result.availableIcons,
        result.availableGradients,
        result.isLoading,
        result.isError,
    ) {
        AddictionCreateUiResult(
            title = result.editTitle,
            description = result.editDescription,
            selectedIcon = result.editIcon,
            selectedGradient = result.editGradient,
            selectedCategory = result.editCategory,
            availableIcons = result.availableIcons,
            availableGradients = result.availableGradients,
            isLoading = result.isLoading,
            isError = result.isError,
        )
    }

    val mappedOnAction: (AddictionCreateUiAction) -> Unit = remember(onAction) {
        { createAction ->
            when (createAction) {
                is AddictionCreateUiAction.ChangeTitle ->
                    onAction(AddictionDetailsUiAction.ChangeTitle(createAction.value))
                is AddictionCreateUiAction.ChangeDescription ->
                    onAction(AddictionDetailsUiAction.ChangeDescription(createAction.value))
                is AddictionCreateUiAction.SelectIcon ->
                    onAction(AddictionDetailsUiAction.SelectIcon(createAction.icon))
                is AddictionCreateUiAction.SelectGradient ->
                    onAction(AddictionDetailsUiAction.SelectGradient(createAction.gradient))
                is AddictionCreateUiAction.SelectCategory ->
                    onAction(AddictionDetailsUiAction.SelectCategory(createAction.category))
                is AddictionCreateUiAction.Submit ->
                    onAction(
                        AddictionDetailsUiAction.SubmitEdit(
                            createAction.emptyTitleError,
                            createAction.emptyCategoryError,
                        )
                    )
                else -> Unit
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(contentPadding)
            .imePadding()
            .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
    ) {
        AddictionCreateFieldsSection(
            result = editResult,
            onAction = mappedOnAction,
            focusManager = focusManager,
        )
        AddictionCreateIconSection(result = editResult, onAction = mappedOnAction)
        AddictionCreateGradientSection(result = editResult, onAction = mappedOnAction)
        AddictionCreateCategorySection(result = editResult, onAction = mappedOnAction)
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))
        AddictionDetailsEditSubmitSection(
            result = editResult,
            onAction = mappedOnAction,
            emptyTitleError = emptyTitleError,
            emptyCategoryError = emptyCategoryError,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Секция с кнопкой сохранения изменений и отображением ошибок валидации.
 *
 * @param result Модель данных формы (совместимая с AddictionCreateUiResult).
 * @param onAction Обработчик действий.
 * @param emptyTitleError Текст ошибки для пустого названия.
 * @param emptyCategoryError Текст ошибки для невыбранной категории.
 */
@Composable
private fun AddictionDetailsEditSubmitSection(
    result: AddictionCreateUiResult,
    onAction: (AddictionCreateUiAction) -> Unit,
    emptyTitleError: String,
    emptyCategoryError: String,
) {
    val warningColor = Tokens.Warning.getThemedColor()

    AnimatedVisibility(
        visible = result.isError != null,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
    ) {
        val errorMessage = when {
            result.isError?.isNetworkError == true -> stringResource(Res.string.addiction_common_error_generic)
            else -> result.isError?.message
        }

        Footnote(
            text = errorMessage ?: stringResource(Res.string.addiction_common_error_generic),
            color = warningColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 6.dp),
        )
    }

    Button(
        text = stringResource(Res.string.addictionDetails_edit_button),
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

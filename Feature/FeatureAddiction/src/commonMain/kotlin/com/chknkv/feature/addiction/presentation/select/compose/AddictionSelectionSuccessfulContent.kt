package com.chknkv.feature.addiction.presentation.select.compose

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addiction_selection_saveFailed_warning
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.chip.Chip
import com.chknkv.designsystem.loading.LoadingHUD
import com.chknkv.designsystem.module.ModuleContent
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import com.chknkv.feature.addiction.models.presentation.select.AddictionGroupUi
import com.chknkv.feature.addiction.models.presentation.select.AddictionSelectionUiAction
import com.chknkv.feature.addiction.models.presentation.select.AddictionSelectionUiResult
import com.chknkv.feature.addiction.presentation.select.elements.SelectionBottomBar
import com.chknkv.feature.addiction.presentation.select.elements.SelectionHeader
import com.chknkv.feature.addiction.presentation.toStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Успешное состояние экрана выбора зависимостей по-дефолту.
 *
 * @param result Состояние содержимого экрана выбора привычек
 * @param onAction Лямбда-обработчик действий пользователя
 */
@Composable
internal fun AddictionSelectionSuccessfulContent(
    result: AddictionSelectionUiResult,
    onAction: (AddictionSelectionUiAction) -> Unit,
) {
    AppScaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                item { SelectionHeader() }

                items(result.groups, key = { it.category }) { group ->
                    SelectionGroup(
                        group = group,
                        selectedIds = result.selectedIds,
                        onToggled = { onAction(AddictionSelectionUiAction.OnAddictionToggled(it)) },
                    )
                }
            }

            AnimatedVisibility(
                visible = result.isFailed,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 },
            ) {
                Footnote(
                    text = stringResource(Res.string.addiction_selection_saveFailed_warning),
                    color = Tokens.Warning.getThemedColor(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                )
            }

            SelectionBottomBar(
                result = result,
                onSkip = { onAction(AddictionSelectionUiAction.OnSkipClicked) },
                onNext = { onAction(AddictionSelectionUiAction.OnNextClicked) },
            )
        }
    }

    if (result.isSaving) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
        ) {
            LoadingHUD()
        }
    }
}

/**
 * Компонент группы привычек с заголовком и сеткой чипсов.
 * 
 * @param group UI-модель группы привычек.
 * @param selectedIds Множество ID выбранных привычек.
 * @param onToggled Коллбэк переключения выбора привычки.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectionGroup(
    group: AddictionGroupUi,
    selectedIds: Set<Int>,
    onToggled: (Int) -> Unit,
) {
    ModuleContent(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(group.category.toStringResource()),
        titlePadding = PaddingValues(start = 30.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
        outPaddingValues = PaddingValues(horizontal = 16.dp),
        innerPaddingValues = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    ) {
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            group.addictions.forEach { addiction ->
                Chip(
                    text = addiction.name,
                    isSelected = addiction.id in selectedIds,
                    onActionHandler = { onToggled(addiction.id) },
                )
            }
        }
    }
}

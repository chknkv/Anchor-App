package com.chknkv.feature.addiction.presentation.all.compose

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_addHabit_button
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_controlDays_label
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_filterAll_label
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_filterEmpty_description
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_limitSheet_button
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_limitSheet_subtitle
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_limitSheet_title
import anchor_app.feature.featureaddiction.generated.resources.ic_cross
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Callout
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonCircle
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.cell.Cell
import com.chknkv.designsystem.cell.CellAction
import com.chknkv.designsystem.chip.Chip
import com.chknkv.designsystem.module.Module
import com.chknkv.designsystem.sheet.Sheet
import com.chknkv.designsystem.sheet.SheetHeightBehavior
import com.chknkv.feature.addiction.models.presentation.AddictionCategoryUi
import com.chknkv.feature.addiction.models.presentation.all.AddictionAllUiResult
import com.chknkv.feature.addiction.models.presentation.all.UserAddictionUi
import com.chknkv.feature.addiction.presentation.toStringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Основной контент экрана всех привычек — фильтр-чипсы по категориям и список привычек.
 *
 * @param result Результат с группами привычек по категориям и флагом доступности создания.
 * @param onAddAddiction Коллбэк для перехода к экрану добавления новой привычки.
 * @param onOpenSettings Коллбэк для перехода в раздел настроек (подписки).
 * @param onInfoAddiction Коллбэк для перехода к экрану с информацией о привычке.
 */
@Composable
internal fun AddictionAllSuccessfulContent(
    result: AddictionAllUiResult,
    onAddAddiction: () -> Unit,
    onOpenSettings: () -> Unit,
    onInfoAddiction: (Int) -> Unit = {},
) {
    var selectedCategory by remember { mutableStateOf<AddictionCategoryUi?>(null) }
    var isLimitSheetVisible by remember { mutableStateOf(false) }

    val filteredAddictions = remember(result.groups, selectedCategory) {
        if (selectedCategory == null) {
            result.groups.flatMap { it.addictions }
        } else {
            result.groups.firstOrNull { it.category == selectedCategory }?.addictions.orEmpty()
        }
    }

    Column {
        Module(outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp)) {
            CellAction(
                title = stringResource(Res.string.addictionAll_addHabit_button),
                onClick = {
                    if (result.isCreateNewAvailable) {
                        onAddAddiction()
                    } else {
                        isLimitSheetVisible = true
                    }
                },
                isDivider = false,
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            item {
                Chip(
                    text = stringResource(Res.string.addictionAll_filterAll_label),
                    isSelected = selectedCategory == null,
                    onActionHandler = { selectedCategory = null },
                )
            }
            items(result.groups) { group ->
                Chip(
                    text = stringResource(group.category.toStringResource()),
                    isSelected = selectedCategory == group.category,
                    onActionHandler = { selectedCategory = group.category },
                )
            }
        }

        if (filteredAddictions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Callout(
                    text = stringResource(Res.string.addictionAll_filterEmpty_description),
                    isSecondary = true,
                )
            }
        } else {
            Module(
                outPaddingValues = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 8.dp),
            ) {
                Column {
                    filteredAddictions.forEachIndexed { index, addiction ->
                        AddictionHabitCell(
                            addiction = addiction,
                            isDivider = index < filteredAddictions.lastIndex,
                            onClick = onInfoAddiction,
                        )
                    }
                }
            }
        }
    }

    Sheet(
        isVisible = isLimitSheetVisible,
        onDismissRequest = { isLimitSheetVisible = false },
        title = stringResource(Res.string.addictionAll_limitSheet_title),
        subtitle = stringResource(Res.string.addictionAll_limitSheet_subtitle),
        actionButton = {
            ButtonCircle(
                onClick = { isLimitSheetVisible = false },
                iconRes = Res.drawable.ic_cross,
            )
        },
        isDragable = true,
        onDragDismissAction = { isLimitSheetVisible = false },
        isOutsideClickEnabled = false,
        heightBehavior = SheetHeightBehavior.WrapContent,
    ) {
        Button(
            text = stringResource(Res.string.addictionAll_limitSheet_button),
            style = ButtonStyle.Action,
            onClick = {
                isLimitSheetVisible = false
                onOpenSettings()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

/**
 * Ячейка одной привычки пользователя в списке.
 *
 * @param addiction UI-модель привычки для отображения.
 * @param isDivider true — отображать разделитель под ячейкой.
 * @param onClick Коллбэк перехода к деталям привычки.
 */
@Composable
private fun AddictionHabitCell(
    addiction: UserAddictionUi,
    isDivider: Boolean,
    onClick: (Int) -> Unit,
) {
    Cell(
        title = addiction.name,
        subtitle = stringResource(addiction.category.toStringResource()),
        iconRes = addiction.iconRes,
        iconGradient = addiction.iconGradient,
        isDivider = isDivider,
        isChevron = true,
        trailingContent = {
            Footnote(
                text = pluralStringResource(
                    Res.plurals.addictionAll_controlDays_label,
                    addiction.controlDays,
                    addiction.controlDays,
                ),
                isSecondary = true,
            )
        },
        onClick = { onClick(addiction.id) },
    )
}

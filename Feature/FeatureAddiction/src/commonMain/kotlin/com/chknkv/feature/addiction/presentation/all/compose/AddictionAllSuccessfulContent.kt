package com.chknkv.feature.addiction.presentation.all.compose

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_addHabit_button
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_controlDays_label
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.cell.Cell
import com.chknkv.designsystem.cell.CellAction
import com.chknkv.designsystem.module.Module
import com.chknkv.designsystem.module.ModuleContent
import com.chknkv.feature.addiction.models.presentation.all.AddictionAllUiResult
import com.chknkv.feature.addiction.models.presentation.all.UserAddictionGroupUi
import com.chknkv.feature.addiction.models.presentation.all.UserAddictionUi
import com.chknkv.feature.addiction.presentation.toTitleStringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Основной контент экрана всех привычек — список категорий, каждая с вложенными привычками.
 *
 * @param result Результат с группами привычек по категориям.
 * @param onAddAddiction Коллбэк для перехода к экрану добавления новой привычки.
 * @param onInfoAddiction Коллбэк для перехода к экрану с информацией о привычке.
 */
@Composable
internal fun AddictionAllSuccessfulContent(
    result: AddictionAllUiResult,
    onAddAddiction: () -> Unit,
    onInfoAddiction: (Int) -> Unit = {},
) {
    Column {
        Module(outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)) {
            CellAction(
                title = stringResource(Res.string.addictionAll_addHabit_button),
                onClick = onAddAddiction,
                isDivider = false,
            )
        }

        result.groups.forEach { group ->
            AddictionHabitGroup(
                group = group,
                onInfoAddiction = onInfoAddiction,
            )
        }
    }
}

/**
 * Секция одной категории: заголовок + список привычек.
 *
 * @param group Группа привычек по категории.
 * @param onInfoAddiction Коллбэк перехода к деталям привычки.
 */
@Composable
private fun AddictionHabitGroup(
    group: UserAddictionGroupUi,
    onInfoAddiction: (Int) -> Unit,
) {
    ModuleContent(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(group.category.toTitleStringResource()),
        outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp),
        innerPaddingValues = PaddingValues(0.dp),
    ) {
        Column {
            group.addictions.forEachIndexed { index, addiction ->
                AddictionHabitCell(
                    addiction = addiction,
                    isDivider = index < group.addictions.lastIndex,
                    onClick = onInfoAddiction,
                )
            }
        }
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
internal fun AddictionHabitCell(
    addiction: UserAddictionUi,
    isDivider: Boolean,
    onClick: (Int) -> Unit,
) {
    Cell(
        title = addiction.name,
        subtitle = stringResource(addiction.category.toTitleStringResource()),
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

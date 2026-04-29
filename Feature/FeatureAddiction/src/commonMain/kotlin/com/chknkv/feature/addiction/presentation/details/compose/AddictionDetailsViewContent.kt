package com.chknkv.feature.addiction.presentation.details.compose

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addictionDetails_controlDays_label
import anchor_app.feature.featureaddiction.generated.resources.addictionDetails_delete_button
import anchor_app.feature.featureaddiction.generated.resources.addictionDetails_incrementDays_button
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Body
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.Title3
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.cell.CellAction
import com.chknkv.designsystem.cell.CellInfo
import com.chknkv.designsystem.module.Module
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiResult
import com.chknkv.feature.addiction.presentation.details.elements.AddictionDetailsActivityCalendar
import com.chknkv.feature.addiction.presentation.toGradientBrush
import com.chknkv.feature.addiction.presentation.toIconDrawableResource
import com.chknkv.feature.addiction.presentation.toTitleStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Контент экрана деталей привычки в режиме просмотра.
 * Отображает статистику, описание и основные параметры привычки без возможности изменения.
 *
 * @param result Результат состояния с данными привычки для отображения.
 * @param onAction Обработчик действий на экране (например, переход к редактированию или удаление).
 * @param contentPadding Отступы, передаваемые от Scaffold (учитывают безопасные зоны и топбар).
 */
@Composable
internal fun AddictionDetailsViewContent(
    result: AddictionDetailsUiResult,
    onAction: (AddictionDetailsUiAction) -> Unit,
    contentPadding: PaddingValues,
) {
    val scrollState = rememberScrollState()
    val gradientBrush = remember(result.gradientKey) { result.gradientKey.toGradientBrush() }
    val onIncrementDays = remember(onAction) { { onAction(AddictionDetailsUiAction.IncrementDays) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(contentPadding),
    ) {
        CellInfo(
            title = result.title,
            subtitle = stringResource(result.category.toTitleStringResource()),
            iconGradient = gradientBrush,
            iconRes = result.iconKey.toIconDrawableResource(),
            outPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        )

        AddictionDetailsActivityCalendar(
            completedDates = result.completedDates,
            gradientKey = result.gradientKey,
        )

        if (result.description.isNotEmpty()) {
            Module(
                outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp),
                innerPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Body(
                    text = result.description,
                    modifier = Modifier.fillMaxWidth(),
                    isSecondary = true,
                    maxLines = Int.MAX_VALUE
                )
            }
        }

        Module(
            outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp),
            innerPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Body(
                    text = stringResource(Res.string.addictionDetails_controlDays_label),
                    isSecondary = true,
                    modifier = Modifier.weight(1f),
                )
                Title3(text = result.controlDays.toString())
            }
        }

        Module(outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp)) {
            CellAction(
                title = stringResource(Res.string.addictionDetails_delete_button),
                isWarning = true,
                isDivider = false,
                onClick = remember(onAction) { { onAction(AddictionDetailsUiAction.DeleteHabit) } }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.fillMaxWidth().height(32.dp))

        if (!result.canIncrementToday && result.nextIncrementSeconds > 0) {
            Footnote(
                text = result.nextIncrementSeconds.toCountdownString(),
                isSecondary = true,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
            )
        }

        Button(
            text = stringResource(Res.string.addictionDetails_incrementDays_button),
            style = ButtonStyle.Action,
            enabled = !result.isLoading && result.canIncrementToday,
            onClick = onIncrementDays,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Преобразует количество секунд в строку обратного отсчета формата ЧЧ:ММ:СС.
 */
private fun Int.toCountdownString(): String {
    val h = this / 3600
    val m = (this % 3600) / 60
    val s = this % 60
    return "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}
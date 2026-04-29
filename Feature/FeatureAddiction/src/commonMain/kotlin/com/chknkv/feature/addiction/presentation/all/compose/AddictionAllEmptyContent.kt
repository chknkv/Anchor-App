package com.chknkv.feature.addiction.presentation.all.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.cell.CellAction
import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_addHabit_button
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_emptyState_description
import com.chknkv.designsystem.module.Module
import org.jetbrains.compose.resources.stringResource

/**
 * Экран пустого состояния — пользователь ещё не добавил ни одной привычки.
 *
 * Отображает текстовую подсказку по центру. Кнопки отсутствуют — навигация
 * к добавлению привычки предоставляется через [onAddAddiction] (управляется родительским экраном).
 *
 * @param onAddAddiction Коллбэк для перехода к экрану добавления новой привычки.
 */
@Composable
internal fun AddictionAllEmptyContent(onAddAddiction: () -> Unit) {
    Column {
        Module(outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp)) {
            CellAction(
                title = stringResource(Res.string.addictionAll_addHabit_button),
                onClick = onAddAddiction,
                isDivider = false
            )
        }

        Module {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp),
                contentAlignment = Alignment.Center
            ) {
                Footnote(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.addictionAll_emptyState_description),
                    isSecondary = true,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
